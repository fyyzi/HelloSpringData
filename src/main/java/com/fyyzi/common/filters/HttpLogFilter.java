package com.fyyzi.common.filters;

import com.fyyzi.common.utils.JsonFormatUtils;
import com.fyyzi.service.HttpLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * 记录 Http request 和 response
 *
 * @author 息阳
 */
@Slf4j
@WebFilter(filterName = "httpLogFilter", urlPatterns = "/*")
public class HttpLogFilter implements Filter {

    @Autowired
    private HttpLogService httpLogService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        sayRequest(request);
        sayResponse(response);
    }

    /**
     * 把请求打印出来
     *
     * @param request {@link ServletRequest}
     */
    private void sayRequest(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String method = req.getMethod();
        Map<String, String[]> parameterMap = req.getParameterMap();
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            String header = req.getHeader(s);
            System.out.println(s + ":" + header);
        }

        log.info("请求路径：{}://{}:{}{} ，请求方式：{}，请求参数：{}，请求头：{}",
                scheme, serverName, serverPort, servletPath, method, parameterMap.size() == 0 ? null : JsonFormatUtils.objectToJson(parameterMap),headerNames);
    }

    /**
     * 吧 response 打印出来
     *
     * @param response {@link ServletResponse}
     */
    private void sayResponse(ServletResponse response) {
        HttpServletResponse rep = (HttpServletResponse) response;
        int status = rep.getStatus();

        log.info("响应状态码:{}", status);
    }
}
