package com.Support.SupportTeam.Security.JWT;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import com.Support.SupportTeam.Security.User.SupportUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final   JwtUtiles jwtUtiles;
    private final SupportUserDetailsService supportUserDetailsService;




    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt =parseJwt(request);

            if(StringUtils.hasText(jwt)&& jwtUtiles.validateToken(jwt)){

                String UserName=jwtUtiles.getUSerNameFromToken(jwt);

                UserDetails userDetails= supportUserDetailsService.loadUserByUsername(UserName);
                Authentication authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(" Invalid or expired token , please login again");
            return;
        }catch (Exception  e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            return;
        }

        filterChain.doFilter(request,response);
    }

    private String parseJwt(HttpServletRequest request){
        String AuthHeader=request.getHeader("Authorization");

        if(StringUtils.hasText(AuthHeader)&&AuthHeader.startsWith("Bearer ")){
         return AuthHeader.substring(7);
        }
        return null;
    }
}
