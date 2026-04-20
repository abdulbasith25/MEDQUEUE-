package com.appointment.config;

public  class learn extends OncePerRequestFilter {

    private final jwtutils jwtUtils;
    private final userdetailsservice userdetailsservice;

    @override
    protected void dofilterinternal(http serverlt request, HttpServletResponse, filterChain) throws serverlessexception, IOException{
        private final string authheader = request.getheader("auth")
        private final string jwt;
        private final string username 

        if authheader == null || !authfilter.startwith("bearer"){
            filterchain.dofilter(request,response, filterchain);
            return;
            jwt = authheader.substring(7);
            username = jwtutils.exctractusername(jwt);
            if username != null && securitycontextholder.getContext.getAuthentication() == null{
                userdetail userdetails = this.userdetailsservice,loadbyusername(username);
                
            }
        }
    }
    
}
