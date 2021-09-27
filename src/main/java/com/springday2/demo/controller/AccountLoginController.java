package com.springday2.demo.controller;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.springday2.demo.entity.Account;
import com.springday2.demo.entity.ResultVO;
import com.springday2.demo.service.impl.AccountLoginServiceImpl;
import com.springday2.demo.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("userLogin")
//@SessionAttributes({"account","accounts"})
public class AccountLoginController {
    @Autowired
    private AccountLoginServiceImpl accountLoginService;
    @Autowired
    private AccountServiceImpl accountService;

    @RequestMapping("login")
    public ModelAndView login(String username, String password, String kaptcha, HttpServletRequest request, ModelAndView modelAndView){

            ResultVO resultVO = accountLoginService.login(username, password);
            //获取正确的验证码
            String kaptchaExpected = (String)request.getSession().getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);

            if (null!=kaptcha){
                if (kaptcha.equalsIgnoreCase(kaptchaExpected)){
                        System.out.println("验证码正确");
                        if (resultVO.isSuccess()){
                            System.out.println("密码正确");
                            /**
                             * 把当前登录的user赋到session域中，供interceptor使用，检查是否登录
                             */
                            Account account = (Account) resultVO.getData();
                            modelAndView.addObject("account",account);
                            if (account.getIsAdmin()==1){
                                modelAndView.addObject("pageNum",1);
                                modelAndView.setViewName("forward:/userLogin/findByPageNo");
                            }else{
                                modelAndView.setViewName("forward:/commonUser.jsp");
                            }
                            return modelAndView;
                        }else {
                            /**
                             * 如果resultVO.isSuccess()为false，或其他判断条件为false则跳到下面下面↓
                             * 用此方法显示错误信息弹窗
                             */
                            System.out.println("用户不存在");
                            String message = resultVO.getMessage();
                            modelAndView.addObject("error","alert('"+message+"');window.location.href='/login/login.html';");
                            modelAndView.setViewName("forward:/login/login.html");
                            return modelAndView;
                        }
                }else{
                    modelAndView.addObject("error","alert('验证码错误!!');window.location.href='/login/login.html';");
                    modelAndView.setViewName("forward:/login/login.html");
                    System.out.println("验证码错误！");
                    return modelAndView;
                }
            }else{
                modelAndView.addObject("error","alert('未输入验证码!!');window.location.href='/login/login.html';");
                modelAndView.setViewName("forward:/login/login.html");
                return modelAndView;
            }
        }

    @RequestMapping("findByPageNo")
    public String findByPageNo(HttpServletRequest request, Model model){
        Integer pageNum = (Integer) request.getAttribute("pageNum");
        if (pageNum==null){
            String pageNum1 = request.getParameter("pageNum");
            int i = Integer.parseInt(pageNum1);
            pageNum =i;
        }
        System.out.println("pageNum = " + pageNum);
        PageHelper.startPage(pageNum,5);
        List<Account> accounts = accountService.selectAll();

        PageInfo pageInfo = new PageInfo(accounts);
        List list = pageInfo.getList();
        for (Object o : list) {
            System.out.println(o);
        }
        model.addAttribute("accounts",list);

//        request.getSession().setAttribute("pageInfo",pageInfo.getList());

//        request.getSession().setAttribute("accounts",list);
        return "admin";
    }


    }

