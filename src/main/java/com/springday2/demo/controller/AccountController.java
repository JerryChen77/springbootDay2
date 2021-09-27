package com.springday2.demo.controller;


import com.github.pagehelper.PageInfo;

import com.springday2.demo.entity.Account;
import com.springday2.demo.service.impl.AccountServiceImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @author Cjl
 * @date 2021/7/10 17:27
 */
@Controller
@RequestMapping("/user")
public class AccountController {
    @Autowired
    public AccountServiceImpl accountService;

    @RequestMapping("/transfer")
    public String transfer(HttpServletRequest request, @RequestParam("fromCardId") Integer fromCardId, @RequestParam("toCardId") Integer toCardId, @RequestParam("money") double money){
        accountService.updateBalance(fromCardId,toCardId,money);
        Account fromUser = accountService.select(fromCardId);
        HttpSession session = request.getSession();
        session.setAttribute("fromUser",fromUser);

        return "redirect:/Success.jsp";
    }

    /**
     *顶部导航条中的头像上传
     */
    @RequestMapping("/upload")
    public String upload(Integer cardId, HttpSession session, MultipartFile img) throws IOException {
        /**
         * 可以用WebApplicationContextUtils.getWebApplicationContext(session.getServletContext())来获取ioc容器
         */
        String originalFilename = img.getOriginalFilename();
        // String extension = FilenameUtils.getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid+"-"+originalFilename;

        Account account = accountService.select(cardId);
//        获取服务器储存文件的绝对路径
        String realPath = session.getServletContext().getRealPath("/files");
//        获取原图片，用于删除原图片
        String originImg = account.getImg();
        File originFIle = new File(realPath+"\\"+originImg);
        if (originFIle.exists()){
            originFIle.delete();
        }

        account.setImg(uniqueFileName);
        accountService.updatePersonalInfo(account);

        session.setAttribute("account",account);

        img.transferTo(new File(realPath+"\\"+uniqueFileName));
        System.out.println("图片上传成功！");

        if (account.getIsAdmin()==1){
            return "forward:/userLogin/findByPageNo";
        }
        return "commonUser";
    }

    @RequestMapping("/download")
    public void download(String filename, HttpSession session, HttpServletResponse response) throws IOException {
        //文件储存的位置
        String realPath = session.getServletContext().getRealPath("files");

        //文件的完整路径
        String fulPath = realPath+"\\"+filename;
        // 设置响应头  告知浏览器，要以附件的形式保存内容   filename=浏览器显示的下载文件名
        response.setHeader("content-disposition","attachment;filename="+filename);

        // 读取目标文件，写出给客户端
        IOUtils.copy(new FileInputStream(fulPath),response.getOutputStream());
    }

    @RequestMapping("/findByCardId")
    @ResponseBody
    public Account findByCardId(Integer cardId, HttpServletRequest request){

        Account account = accountService.select(cardId);
//        此步是用户更新页面获取被更新的user原数据，但是不好，会增加服务器负担。
//        下次可以多做几个控制器，将具体user储存在request中，转发给具体页面，
        request.getSession().setAttribute("adminFoundUser",account);
        return account;
    }

    @RequestMapping("/register")
    public String register(HttpServletRequest request, MultipartFile image) throws InvocationTargetException, IllegalAccessException, IOException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Account account = new Account();
        BeanUtils.populate(account,parameterMap);

        //若上传了图片则上传至服务器
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("image");
        if (!file.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
            String uniqueFileName = uuid+"-"+originalFilename;
            String realPath = request.getSession().getServletContext().getRealPath("files");
            image.transferTo(new File(realPath+"\\"+uniqueFileName));
            System.out.println("图片上传成功！");
            account.setImg(uniqueFileName);
        }else
        {
            account.setImg("img1.jpg");
        }

        accountService.save(account);
        List<Account> accounts = accountService.selectAll();
        request.getSession().setAttribute("accounts",accounts);
        return "redirect:/admin.html";
    }
//    管理员页面删除用户控制器
    @DeleteMapping("/users/{id}")
    @ResponseBody
    public String delete(@PathVariable Integer id){
        Integer delete = accountService.delete(id);
        if (delete>0){
            return "success";
        }
        return "failed";
    }


    @RequestMapping("/update")
    public String update(String updateFrom, Account account, HttpServletRequest request, MultipartFile image) throws IOException{

        //此步用户判断是否上传了文件，若上传了文件则上传至服务器
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("image");
        if (!file.isEmpty()) {
            String realPath = request.getSession().getServletContext().getRealPath("files");
            //        获取原图片，用于删除原图片
            String originImg = account.getImg();
            File originFIle = new File(realPath+"\\"+originImg);
            if (originFIle.exists()){
                originFIle.delete();
            }
            String originalFilename = image.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String uniqueFileName = uuid+"-"+originalFilename;
            image.transferTo(new File(realPath+"\\"+uniqueFileName));
            System.out.println("图片上传成功！");
            account.setImg(uniqueFileName);
        }
        accountService.updateAllInfos(account);
        Account loginUser = (Account) request.getSession().getAttribute("account");
        if (loginUser.getCardId().equals(account.getCardId())){
            request.getSession().setAttribute("account",account);
        }

        if (updateFrom.equals("admin")){
            PageInfo pageInfo = (PageInfo) request.getSession().getAttribute("pageInfo");

            int pageNum = pageInfo.getPageNum();
            request.setAttribute("pageNum",pageNum);
            return "forward:/userLogin/findByPageNo";
        }
        request.getSession().setAttribute("account",account);
        return "commonUser";
    }
}
