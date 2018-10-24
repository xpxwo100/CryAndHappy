package com.umfuck.demo.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter implements Filter {
    //不需要登录就可以访问的路径(比如:注册登录等)
    String[] includeUrls = new String[]{"/login","register"};
    //标示符：表示当前用户未登录(可根据自己项目需要改为json样式)
    String NO_LOGIN = "您还未登录";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String uri = request.getRequestURI();
        String strUrl = "http://" + request.getServerName() // 服务器地址
                + ":" + request.getServerPort() // 端口号
                + request.getContextPath();
        //是否需要过滤
        boolean needFilter = isNeedFilter(uri);
        if (!needFilter) { //不需要过滤直接传给下一个过滤器
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }else{
            if(session!=null&&session.getAttribute("user") != null){
                String id = (String) session.getAttribute("user");
                System.out.println(id);
                filterChain.doFilter(request, response);
            }else{
                String requestType = request.getHeader("X-Requested-With");
                //判断是否是ajax请求
                if(requestType!=null && "XMLHttpRequest".equals(requestType)){
                    response.getWriter().write(this.NO_LOGIN);
                }else{
                    //重定向到登录页(需要在static文件夹下建立此html文件)
                    response.sendRedirect(request.getContextPath()+"/login.html");
                }
                return;
            }
        }




    }
    /**
     * @Author: xxxxx
     * @Description: 是否需要过滤
     * @Date: 2018-03-12 13:20:54
     * @param uri
     */
    public boolean isNeedFilter(String uri) {

        for (String includeUrl : includeUrls) {
            if(uri.contains(includeUrl.trim())) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void destroy() {

    }
}
