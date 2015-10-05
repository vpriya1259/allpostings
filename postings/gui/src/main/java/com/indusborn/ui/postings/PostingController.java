/* ******************************************************************************
 * Copyright (C) 2010 qadda, Inc. All Rights Reserved
 ******************************************************************************/

/**
 * PostingController.java --
 * <p>
 * Handles all the URL related to the postings
 * <p>
 * 
 * @author vpriya1259
 */
package com.indusborn.ui.postings;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.flash.FlashMap;
import org.springframework.web.flash.Message;
import org.springframework.web.flash.MessageType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.indusborn.common.Constants;
import com.indusborn.common.ErrorObject;
import com.indusborn.common.MessageKeys;
import com.indusborn.common.MessageValues;
import com.indusborn.common.NonImageContentException;
import com.indusborn.common.ResultObject;
import com.indusborn.domain.Posting;
import com.indusborn.domain.PostingImage.Status;
import com.indusborn.service.PostingService;
import com.indusborn.ui.domain.ImageVO;
import com.indusborn.ui.domain.MailVO;
import com.indusborn.ui.domain.PaginationVO;
import com.indusborn.ui.domain.PostingVO;
import com.indusborn.ui.domain.PostingsVO;
import com.indusborn.ui.domain.RegionVO;
import com.indusborn.ui.domain.SubCatgries;
import com.indusborn.ui.domain.UserVO;
import com.indusborn.ui.mail.SendMailForm;
import com.indusborn.ui.util.GeneralUtil;
import com.indusborn.ui.util.PaginationUtil;
import com.indusborn.util.CatgryUtil;
import com.indusborn.util.ImageUtil;
import com.indusborn.util.TimeUtil;

@Controller
public class PostingController {
   private static Log log = LogFactory.getLog(PostingController.class);

   @Autowired
   private PostingService postingService;

   @RequestMapping("/")
   public String home(HttpServletRequest request) {
      Device currentDevice = DeviceUtils.getCurrentDevice(request);
      if (currentDevice.isMobile()) {
         return "redirect:/m";
      }
      UserVO userVO = (UserVO) request.getAttribute("userVO");
      if (userVO != null) {
         return "redirect:/usr/new";
      }
      return "redirect:/posts/posting";
   }

   @RequestMapping("/posts/{postingCatgry}")
   public ModelAndView postings(@PathVariable String postingCatgry,
         HttpServletRequest request, Model model) {
      return postingsHelper(postingCatgry, request, model);
   }

   public ModelAndView postingsHelper(String postingCatgry,
         HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.all");
      model.addAttribute("radii", GeneralUtil.getRadii());
      model.addAttribute("catgries", CatgryUtil.getAllMainCatgries());
      Class<? extends Posting> posting =
            CatgryUtil.getPostingClass(postingCatgry);

      if (posting == null) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.INVALID_REQUEST));
         return resultView;
      }

      ResultObject result = postings(posting, request);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingsVO postingsVO = (PostingsVO) result.getData();

      if (postingsVO.getPostingVOs().size() == 0) {
         model.addAttribute("message", new Message(MessageType.INFO,
               MessageValues.POSTING_LIST_EMPTY));
         return resultView;
      }

      PaginationVO paginationVO =
            PaginationUtil.createNavPages(postingsVO.getPageNum(),
                  postingsVO.getPageSize(), postingsVO.getTotal(),
                  Constants.PAGINATION_SIZE);

      model.addAttribute("postingsVO", postingsVO);
      model.addAttribute("paginationVO", paginationVO);
      return resultView;
   }

   @RequestMapping("/usr/all")
   public ModelAndView postingUsrAll(HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.usr.all");
      ResultObject result = usrPostings(request);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingsVO postingsVO = (PostingsVO) result.getData();

      if (postingsVO.getPostingVOs().size() == 0) {
         model.addAttribute("message", new Message(MessageType.INFO,
               MessageValues.POSTING_LIST_EMPTY));
         return resultView;
      }

      PaginationVO paginationVO =
            PaginationUtil.createNavPages(postingsVO.getPageNum(),
                  postingsVO.getPageSize(), postingsVO.getTotal(),
                  Constants.PAGINATION_SIZE);

      model.addAttribute("postingVOs", postingsVO.getPostingVOs());
      model.addAttribute("paginationVO", paginationVO);
      return resultView;
   }

   @RequestMapping(value = "/posts/search")
   public ModelAndView postingsSearch(HttpServletRequest request, Model model) {
      return postingsSearchHelper(request, model);
   }

   public ModelAndView postingsSearchHelper(HttpServletRequest request,
         Model model) {
      Device currentDevice = DeviceUtils.getCurrentDevice(request);
      ModelAndView resultView;
      if (currentDevice.isMobile()) {
         resultView = new ModelAndView("m.posting.search");
      } else {
         resultView = new ModelAndView("posting.all");
      }
      model.addAttribute("radii", GeneralUtil.getRadii());
      model.addAttribute("catgries", CatgryUtil.getAllMainCatgries());

      ResultObject result = searchPostings(request);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingsVO postingsVO = (PostingsVO) result.getData();
      model.addAttribute("postingsVO", postingsVO);      
      if (postingsVO.getPostingVOs().size() == 0) {
         model.addAttribute("message", new Message(MessageType.INFO,
               MessageValues.POSTING_LIST_EMPTY));
         return resultView;
      }

      PaginationVO paginationVO =
            PaginationUtil.createNavPages(postingsVO.getPageNum(),
                  postingsVO.getPageSize(), postingsVO.getTotal(),
                  Constants.PAGINATION_SIZE);
      if (currentDevice.isMobile()) {
         resultView = new ModelAndView("m.posting.all");
      } else {
         resultView = new ModelAndView("posting.all");
      }
      model.addAttribute("paginationVO", paginationVO);
      return resultView;
   }

   @RequestMapping(value = "/posts/{catgry}/{id}", method = RequestMethod.GET)
   public ModelAndView posting(@PathVariable String catgry,
         @PathVariable Long id, HttpServletRequest request, Model model) {
      Device currentDevice = DeviceUtils.getCurrentDevice(request);
      ModelAndView resultView;
      if (currentDevice.isMobile()) {
         resultView = new ModelAndView("m.posting");
      } else {
         resultView = new ModelAndView("posting");
      }

      ResultObject result = fetchPosting(catgry, id);
      String title = "Posting not found!";
      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         model.addAttribute("title", title);
         return resultView;
      }
      PostingVO postingVO = (PostingVO) result.getData();
      title = postingVO.getTitle();
      model.addAttribute("title", title);      
      model.addAttribute("postingVO", postingVO);
      return resultView;
   }

   @RequestMapping(value = "/posts/mail/{catgry}/{id}", method = RequestMethod.GET)
   public ModelAndView postingMail(@PathVariable String catgry,
         @PathVariable Long id, HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.mail");
      model.addAttribute("sendMailForm", new SendMailForm());
      model.addAttribute("postingId", id);
      model.addAttribute("catgry", catgry);
      return resultView;
   }

   @RequestMapping(value = "/posts/mail/{catgry}/{id}", method = RequestMethod.POST)
   public ModelAndView postingMailPost(@PathVariable String catgry,
         @PathVariable Long id, @Valid SendMailForm sendMailForm,
         BindingResult formBinding, Model model) {
      ModelAndView resultView = new ModelAndView("posting.mail");
      model.addAttribute("sendMailForm", new SendMailForm());
      model.addAttribute("postingId", id);
      model.addAttribute("catgry", catgry);

      //Form validation
      if (formBinding.hasErrors()) {
         return resultView;
      }

      //Fetch the posting
      ResultObject result = fetchPosting(catgry, id);
      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingVO postingVO = (PostingVO) result.getData();

      //Send the anonymous email
      result = sendAnonymContactMail(sendMailForm, postingVO);
      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }
      resultView =
            new ModelAndView("redirect:/posts/" + postingVO.getCatgry() + "/"
                  + postingVO.getId());
      FlashMap.setSuccessMessage(MessageValues.POSTING_SEND_ANONYM_MAIL_SUCC);

      return resultView;
   }

   /**
    * Delivers an email to the posting owner
    * 
    * @param sendMailForm
    * @param postingVO
    * @return
    */
   public ResultObject sendAnonymContactMail(SendMailForm sendMailForm,
         PostingVO postingVO) {
      MailVO mailVO = new MailVO();
      mailVO.setAnonymName(sendMailForm.getName());
      mailVO.setAnonymAddress(sendMailForm.getEmail());
      mailVO.setPhoneNum(sendMailForm.getPhoneNumber());
      mailVO.setSubject(postingVO.getTitle());
      mailVO.setMessageBody(sendMailForm.getMessage());
      mailVO.setToAddress(postingVO.getEmail());
      ResultObject result = postingService.sendAnonymContactMail(mailVO);
      return result;
   }

   @RequestMapping(value = "/usr/new", method = RequestMethod.GET)
   public ModelAndView postingNewGet(HttpServletRequest request, Model model) {
      model.addAttribute("catgries", CatgryUtil.getMainCatgries());
      model.addAttribute("catgry", Constants.RENTAL);
      model.addAttribute("subCatgries",
            CatgryUtil.getSubCatgries(Constants.RENTAL));
      model.addAttribute("currencyVOs", GeneralUtil.getCurrencies());
      ModelAndView resultView = new ModelAndView("posting.usr.new");
      return resultView;
   }

   @RequestMapping(value = "/usr/new", method = RequestMethod.POST)
   public ModelAndView postingNewPost(HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.usr.new");
      model.addAttribute("catgries", CatgryUtil.getMainCatgries());      
      model.addAttribute("catgry", Constants.RENTAL);
      model.addAttribute("subCatgries",
            CatgryUtil.getSubCatgries(Constants.RENTAL));
      model.addAttribute("currencyVOs", GeneralUtil.getCurrencies());

      UserVO userVO = (UserVO) request.getAttribute("userVO");
      PostingVO postingVO = null;
      try {
         postingVO = getPostingVO(request, userVO);
         postingVO.setCreated(TimeUtil.getCurrTimeInUTC());
         postingVO.setUpdated(TimeUtil.getCurrTimeInUTC());
      } catch (NonImageContentException ex) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.NON_IMAGE_CONTENT));
         return resultView;
      }

      ResultObject result = postingService.addPosting(postingVO);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      FlashMap.setSuccessMessage(MessageValues.POSTING_ADD_SUCC);
      resultView = new ModelAndView("redirect:/usr/all");
      return resultView;
   }

   @RequestMapping(value = "/usr/upd/{postingId}", method = RequestMethod.GET)
   public ModelAndView postingUsrUpdGet(@PathVariable Long postingId,
         HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.usr.upd");

      model.addAttribute("catgries", CatgryUtil.getMainCatgries());
      model.addAttribute("currencyVOs", GeneralUtil.getCurrencies());
      model.addAttribute("maxImages", Constants.MAX_IMAGES);
      UserVO userVO = (UserVO) request.getAttribute("userVO");

      ResultObject result = postingService.getPosting(postingId);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingVO postingVO = (PostingVO) result.getData();

      if (postingVO.getOwnerId() != userVO.getId()) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.POSTING_UPD_NOT_PERMITTED));
         return resultView;
      }

      model.addAttribute("postingVO", postingVO);
      model.addAttribute("subCatgries",
            CatgryUtil.getSubCatgries(postingVO.getCatgry()));
      return resultView;
   }

   @RequestMapping(value = "/usr/upd/{postingId}", method = RequestMethod.POST)
   public ModelAndView postingUsrUpdPost(@PathVariable Long postingId,
         HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.usr.upd");
      UserVO userVO = (UserVO) request.getAttribute("userVO");

      ResultObject result = postingService.getPosting(postingId);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingVO postingVO = (PostingVO) result.getData();

      if (postingVO.getOwnerId() != userVO.getId()) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.POSTING_UPD_NOT_PERMITTED));
         return resultView;
      }
      
      String postingEmail = postingVO.getEmail();

      try {
         postingVO = getPostingVO(request, userVO);
      } catch (NonImageContentException ex) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.NON_IMAGE_CONTENT));
         return resultView;
      }

      postingVO.setId(postingId);
      postingVO.setEmail(postingEmail);
      postingVO.setUpdated(TimeUtil.getCurrTimeInUTC());

      result = postingService.addPosting(postingVO);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      FlashMap.setSuccessMessage(MessageValues.POSTING_UPD_SUCC);
      resultView = new ModelAndView("redirect:/usr/all");
      return resultView;
   }

   @RequestMapping(value = "/usr/del/{postingId}", method = RequestMethod.GET)
   public ModelAndView postingDel(@PathVariable Long postingId,
         HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.usr.del");
      UserVO userVO = (UserVO) request.getAttribute("userVO");
      ResultObject result = postingService.getPosting(postingId);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingVO postingVO = (PostingVO) result.getData();

      if (postingVO.getOwnerId() != userVO.getId()) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.POSTING_UPD_NOT_PERMITTED));
         return resultView;
      }
      model.addAttribute("postingVO", postingVO);
      return resultView;
   }

   @RequestMapping(value = "/usr/del/con/{postingId}", method = RequestMethod.GET)
   public ModelAndView postingDelConfirm(@PathVariable Long postingId,
         HttpServletRequest request, Model model) {
      ModelAndView resultView = new ModelAndView("posting.usr.del");
      UserVO userVO = (UserVO) request.getAttribute("userVO");
      ResultObject result = postingService.getPosting(postingId);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      PostingVO postingVO = (PostingVO) result.getData();

      if (postingVO.getOwnerId() != userVO.getId()) {
         model.addAttribute("message", new Message(MessageType.ERROR,
               MessageValues.POSTING_UPD_NOT_PERMITTED));
         return resultView;
      }

      result = postingService.deletePosting(postingId);

      if (result.hasError()) {
         model.addAttribute("message",
               GeneralUtil.getMessage(result.getError()));
         return resultView;
      }

      FlashMap.setSuccessMessage(MessageValues.POSTING_DEL_SUCC);
      result = postingService.getUserPostingsSize(userVO.getId());
      if (result.hasError()) {
         resultView = new ModelAndView("redirect:/usr/new");
      }

      Long size = (Long) result.getData();

      if (size > 0) {
         resultView = new ModelAndView("redirect:/usr/all");
      } else {
         resultView = new ModelAndView("redirect:/usr/new");
      }
      return resultView;
   }

   @RequestMapping(value = "/posts/subcatgries", method = RequestMethod.GET)
   public @ResponseBody
   SubCatgries getPostsSubCatgries(@RequestParam String catgry) {
      List<String> catgries = CatgryUtil.getSubCatgries(catgry);
      SubCatgries subCatgries = new SubCatgries(catgries);
      return subCatgries;
   }

   @RequestMapping("/m")
   public String mHome(HttpServletRequest request) {
      UserVO userVO = (UserVO) request.getAttribute("userVO");
      if (userVO != null) {
         return "redirect:/usr/new";
      }
      return "redirect:/m/posts/search";
   }

   @RequestMapping(value = "/m/posts/search", method = RequestMethod.GET)
   public ModelAndView postingsSearch(Model model) {
      ModelAndView resultView = new ModelAndView("m.posting.search");
      model.addAttribute("radii", GeneralUtil.getRadii());
      model.addAttribute("catgries", CatgryUtil.getAllMainCatgries());
      return resultView;
   }

   private ResultObject postings(Class<? extends Posting> posting,
         HttpServletRequest request) {
      ResultObject result = new ResultObject();
      //Check for the mandatory fields
      if (request == null) {
         result.addError(new ErrorObject(MessageKeys.INVALID_POSTINGS_REQUEST));
         return result;
      }

      //Set page number
      String strPageNum = request.getParameter(Constants.FORM_FIELD_PAGE_NUM);
      int pageNum = Constants.POSTINGS_FIRST_PAGE;
      if (strPageNum != null) {
         try {
            pageNum = Integer.parseInt(strPageNum);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }

      //Set page size
      String strPageSize = request.getParameter(Constants.FORM_FIELD_PAGE_SIZE);
      int pageSize = Constants.POSTINGS_PER_PAGE;
      if (strPageSize != null) {
         try {
            pageSize = Integer.parseInt(strPageSize);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }
      result = postingService.getPostings(posting, pageNum, pageSize);
      return result;
   }

   private ResultObject usrPostings(HttpServletRequest request) {
      ResultObject result = new ResultObject();

      //Check for the mandatory fields
      if (request == null) {
         result.addError(new ErrorObject(
               MessageKeys.INVALID_USR_POSTINGS_REQUEST));
         return result;
      }

      UserVO userVO = (UserVO) request.getAttribute("userVO");

      //Set page number
      String strPageNum = request.getParameter(Constants.FORM_FIELD_PAGE_NUM);
      int pageNum = Constants.POSTINGS_FIRST_PAGE;
      if (strPageNum != null) {
         try {
            pageNum = Integer.parseInt(strPageNum);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }

      //Set page size
      String strPageSize = request.getParameter(Constants.FORM_FIELD_PAGE_SIZE);
      int pageSize = Constants.POSTINGS_PER_PAGE;
      if (strPageSize != null) {
         try {
            pageSize = Integer.parseInt(strPageSize);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }
      result =
            postingService.getUserPostings(userVO.getId(), pageNum, pageSize);
      return result;
   }


   private ResultObject fetchPosting(String catgry, Long postingId) {
      ResultObject result = new ResultObject();
      //Validate
      if (catgry == null || !CatgryUtil.validCatgry(catgry)) {
         /*result.addError(new ErrorObject(MessageKeys.INVALID_CATGRY));
         return result;*/
         catgry = Constants.POSTING;
      }
      if (postingId == null) {
         result.addError(new ErrorObject(MessageKeys.INVALID_POSTING_ID));
         return result;
      }
      //Fetch
      result = postingService.getPosting(postingId);
      return result;
   }


   private PostingVO getPostingVO(HttpServletRequest request, UserVO userVO)
         throws NonImageContentException {
      MultipartHttpServletRequest multiRequest =
            (MultipartHttpServletRequest) request;

      String catgry = multiRequest.getParameter(Constants.FORM_FIELD_CATGRY);
      String subCatgry =
            multiRequest.getParameter(Constants.FORM_FIELD_SUB_CATGRY);
      String type = multiRequest.getParameter(Constants.FORM_FIELD_TYPE);
      String address = multiRequest.getParameter(Constants.FORM_FIELD_ADDRESS);
      String title = multiRequest.getParameter(Constants.FORM_FIELD_TITLE);
      String desc = multiRequest.getParameter(Constants.FORM_FIELD_DESC);
      String currency =
            multiRequest.getParameter(Constants.FORM_FIELD_CURRENCY);
      String price = multiRequest.getParameter(Constants.FORM_FIELD_PRICE);
      String anonym = multiRequest.getParameter("anonym");

      PostingVO postingVO = new PostingVO();
      postingVO.setOwnerId(userVO.getId());
      postingVO.setCatgry(catgry);
      postingVO.setSubCatgry(subCatgry);
      postingVO.setType(type);
      postingVO.setAddress(address);
      postingVO.setTitle(title);
      postingVO.setDesc(desc);
      postingVO.setCurrency(currency);
      postingVO.setPrice(price);
      postingVO.setEmail(userVO.getEmail());
      if (anonym != null && anonym.equalsIgnoreCase("On")) {
         postingVO.setFuzzyEmail(true);
      }
      RegionVO regionVO = GeneralUtil.getRegionVO(multiRequest);
      postingVO.setLat(regionVO.getLat());
      postingVO.setLng(regionVO.getLng());
      postingVO.setRegionVO(regionVO);

      MultipartFile multipartFile = null;
      for (Object obj : multiRequest.getFileMap().values()) {
         multipartFile = (MultipartFile) obj;
         if (multipartFile == null || multipartFile.getSize() == 0) {
            continue;
         }

         if (!multipartFile.getContentType().contains("image")) {
            throw new NonImageContentException();
         }

         log.debug("File content type: " + multipartFile.getContentType());
         ImageVO imageVO = ImageUtil.getImage(multipartFile);
         if (imageVO != null) {
            postingVO.getImageVOs().add(imageVO);
         }
      }

      //Get update posting image VOs
      List<ImageVO> updImageVOs = getUpdateImageVOs(request);
      if (updImageVOs != null && updImageVOs.size() > 0) {
         postingVO.getImageVOs().addAll(updImageVOs);
      }

      if (catgry.equalsIgnoreCase(Constants.RENTAL)) {
         //Rental specific fields
      } else if (catgry.equalsIgnoreCase(Constants.ROOMMATE)) {
         //Rommate specific fields
      }
      return postingVO;
   }

   private List<ImageVO> getUpdateImageVOs(HttpServletRequest request) {
      List<ImageVO> imageVOs = new ArrayList<ImageVO>();
      ImageVO imageVO = null;
      MultipartHttpServletRequest multiRequest =
            (MultipartHttpServletRequest) request;

      for (int i = 0; i < Constants.MAX_IMAGES; i++) {
         String imageStatusVar = "image" + i + "Status";
         String strImageId = multiRequest.getParameter(imageStatusVar);
         if (strImageId == null) {
            continue;
         }
         long imageId = getImageId(strImageId);
         if (imageId == 0) {
            continue;
         }

         imageVO = new ImageVO();
         imageVO.setId(imageId);
         if (strImageId.contains(Constants.IMG_STATUS_DELETE)) {
            imageVO.setStatus(Status.DELETE);
         } else if (strImageId.contains(Constants.IMG_STATUS_KEEP)) {
            imageVO.setStatus(Status.KEEP);
         }
         imageVOs.add(imageVO);
      }
      return imageVOs;
   }

   private long getImageId(String status) {
      String strId = null;
      if (status.contains(Constants.IMG_STATUS_KEEP)) {
         strId =
               status.substring(status.indexOf(Constants.IMG_STATUS_KEEP)
                     + Constants.IMG_STATUS_KEEP.length());
      } else if (status.contains(Constants.IMG_STATUS_DELETE)) {
         strId =
               status.substring(status.indexOf(Constants.IMG_STATUS_DELETE)
                     + Constants.IMG_STATUS_DELETE.length());
      }
      if (strId == null) {
         return 0;
      }

      long longId = 0;
      try {
         longId = Long.valueOf(strId);
      } catch (Exception ex) {
         log.warn("Error converting the string to image id.");
      }
      return longId;
   }

   public ResultObject searchPostings(HttpServletRequest request) {
      ResultObject result = new ResultObject();
      //Check for the mandatory fields
      if (request == null) {
         result.addError(new ErrorObject(MessageKeys.INVALID_SEARCH_REQUEST));
         return result;
      }

      String locationText =
            request.getParameter(Constants.FORM_FIELD_LOCATION_TEXT);
      String catgry = request.getParameter(Constants.FORM_FIELD_CATGRY);

      if (catgry == null || !CatgryUtil.validCatgry(catgry)) {
/*         result.addError(new ErrorObject(MessageKeys.INVALID_CATGRY));
         return result;
*/    
         catgry = Constants.POSTING; //Default, search in all categories
      }

      if (locationText == null || locationText.isEmpty()) {
         result.addError(new ErrorObject(MessageKeys.INVALID_LOCATION));
         return result;
      }

      PostingsVO reqPostingsVO = new PostingsVO();
      reqPostingsVO.setCatgry(catgry);
      reqPostingsVO.setLocationText(locationText);

      //Set search text
      String searchText =
            request.getParameter(Constants.FORM_FIELD_SEARCH_TEXT);
      if (searchText != null) {
         reqPostingsVO.setSearchText(searchText);
      }

      //Set radius
      String strRadius = request.getParameter(Constants.FORM_FIELD_RADIUS);
      Integer radius = Constants.DEFAULT_SEARCH_RADIUS;
      if (strRadius != null) {
         try {
            radius = Integer.parseInt(strRadius);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }
      reqPostingsVO.setRadius(radius);

      //Set sort by
      String sortBy = request.getParameter(Constants.FORM_FIELD_SORTBY);
      if (sortBy == null) {
         sortBy = Constants.DEFAULT_SORTBY_VALUE;
      }
      reqPostingsVO.setSortBy(sortBy);

      //Set page number
      String strPageNum = request.getParameter(Constants.FORM_FIELD_PAGE_NUM);
      int pageNum = Constants.POSTINGS_FIRST_PAGE;
      if (strPageNum != null) {
         try {
            pageNum = Integer.parseInt(strPageNum);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }
      reqPostingsVO.setPageNum(pageNum);

      //Set page size
      String strPageSize = request.getParameter(Constants.FORM_FIELD_PAGE_SIZE);
      int pageSize;
      Device currDevice = DeviceUtils.getCurrentDevice(request);
      if (currDevice.isMobile()) {
         pageSize = Constants.M_POSTINGS_PER_PAGE;
      } else {
         pageSize = Constants.POSTINGS_PER_PAGE;
      }
      if (strPageSize != null) {
         try {
            pageSize = Integer.parseInt(strPageSize);
         } catch (NumberFormatException ex) {
            //NOTHING
         }
      }
      reqPostingsVO.setPageSize(pageSize);

      //Set region VO
      RegionVO regionVO = GeneralUtil.getRegionVO(request);
      if (regionVO != null) {
         //Cache region VO for further requests
         GeneralUtil.putRegionVO(locationText, regionVO);
      } else {
         //Fetch it from the cache
         regionVO = GeneralUtil.getRegionVO(locationText);
         if (regionVO == null) {
            regionVO = new RegionVO();
         }
      }
      reqPostingsVO.setRegionVO(regionVO);
      result = postingService.search(reqPostingsVO);
      return result;
   }

}
