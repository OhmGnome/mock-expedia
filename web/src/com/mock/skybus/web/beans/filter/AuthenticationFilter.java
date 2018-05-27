package com.mock.skybus.web.beans.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.mock.skybus.web.beans.AuthBean;

/**
 * Verifies that the user's role allows them to navigate where they are
 * attempting to. A Servlet Filter object implements an interface that specifies
 * these three methods: init, doFilter, and destroy. The first and the last
 * allow for custom processing at the beginning and end of the object's life
 * cycle. To enable a Filter to initialize itself, an instance of FilterConfig
 * is passed to its init method. However, doFilter is where the Filter does its
 * job. doFilter accepts objects of type ServletRequest and ServletResponse
 * (which usually need to be cast to their HTTP versions: HttpServletRequest and
 * HttpServletReponse), and a FilterChain object that contains a chain of
 * Filters to execute after the current Filter finishes its work.
 * 
 * @author ZGT43
 *
 */
@Component
@Scope("request")
public class AuthenticationFilter extends GenericFilterBean {

	@Autowired
	private AuthBean authBean;

	/**
	 * Intercept requests to verify that a user's role or roles allows them to
	 * go where they are attempting to go. After the filter intercepts the
	 * requests, doFilter calls a Java class that actually performs the work.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (authBean.isLoggedIn()) {
			chain.doFilter(request, response);
		} else {
			if (response instanceof HttpServletResponse) {
				((HttpServletResponse) response).sendRedirect(String.format(
						"%s/login.xhtml",
						((HttpServletRequest) request).getContextPath()));
			}
		}

	}
}
