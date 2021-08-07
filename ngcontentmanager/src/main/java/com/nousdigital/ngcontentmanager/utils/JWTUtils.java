package com.nousdigital.ngcontentmanager.utils;
import com.nousdigital.ngcontentmanager.BuildConfig;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import javax.crypto.SecretKey;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import timber.log.Timber;
public class JWTUtils {
    //TODO: GET CORRECT KEY
    public static String getJwtTokenForApi(){
        Timber.d("TODO: GET CORRECT JWT KEYS!!!");
//        return createJWT("inhouse-guide",
//                "nousdigital",
//                BuildConfig.JWT_API,
//                BuildConfig.JWT_API_TENANT);
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLZHZQZnEwdzFUUEpnbkpKVThZZXppYThkUG8yVkhBQWV0SXBDMGN3Yl9rIn0.eyJleHAiOjE2MzM0MjYxNzUsImlhdCI6MTYyODI0MjE3NSwianRpIjoiNTBjZjliNGYtYzRmZC00ZmMyLWEyYzMtMjRiMWZhNzJkOThkIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLXN0YWdlLm5vdXNkaWdpdGFsLm5ldC9hdXRoL3JlYWxtcy9zbWFlayIsInN1YiI6IjJmMzc5MDAwLWFmNGEtNGViNC1iMmQ1LWM5NzMyNmM2ZTM3YyIsInR5cCI6IkJlYXJlciIsImF6cCI6Imd1aWRlIiwic2Vzc2lvbl9zdGF0ZSI6IjkwZTc0ODE1LTgxZGItNDJhZS04MzNmLWQ3YmEyYjhiOWYxZCIsImFjciI6IjEiLCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIiwiY2xpZW50SWQiOiJndWlkZSIsImNsaWVudEhvc3QiOiI4NC4xMTQuODEuMTc3IiwicGVybWlzc2lvbnMiOlsibm91cy5jbXMuc2VydmVyLmNvbmZpZy5yZWFkIiwibm91cy5jbXMuZmlsZS5yZWFkIiwibm91cy5jbXMudG91ci5yZWFkIiwibm91cy5jbXMuZ3JvdXAucmVhZCIsIm5vdXMuY21zLm1hcC5yZWFkIiwibm91cy5jbXMuY29udGVudC5yZWFkIiwibm91cy5jbXMuZXhoaWJpdGlvbi5yZWFkIiwibm91cy5jbXMuaG90c3BvdC5yZWFkIiwibm91cy5jbXMuc3RhdGlvbi5yZWFkIiwibm91cy5jbXMuY2hhbm5lbC5yZWFkIiwibm91cy5zeW5jLnJlYWQiLCJub3VzLnN5bmMuY2hhbm5lbC5yZWFkIl0sInRlbmFudElkIjoiNjk2MmNlYzAtOTgyZC00YmQ2LWI2YTAtZGU5NjEwMGIwZWQwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWd1aWRlIiwiY2xpZW50QWRkcmVzcyI6Ijg0LjExNC44MS4xNzcifQ.JjECg-j-FiY_Uk1RbW17zFGvXy8oz9ntvKrq0VrCuTk99TTb-jny73yV6F6hbJUb6F6pLaJjzzlgELiGid9dW9mN8EVUUncMB635sG89PflikYqP3v9cyHk543UqRFmG_Hpqwknji1CZ2Uajtz8hVRuiIkw3iEQ6MIgNbnLHofyHI1wNETvm0Yahe1J_AtKBSDZRZJj5HVtcwHpty92B_hMRxFSX9v42EMiCaeyVSRLkbunCtt_6JOzoAVnNgJxnNiV1pgbRqoS-V4z0a68BW7WOj7C7Q4yHCyaOZbkWPILiF1pozCzeEdHKFxT5JRVezXKUJYPbTI5UbXnlQL7zWg";
    }
    //TODO: GET CORRECT KEY
    public static String getJwtTokenForSyncServer(){
        return "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLZHZQZnEwdzFUUEpnbkpKVThZZXppYThkUG8yVkhBQWV0SXBDMGN3Yl9rIn0.eyJleHAiOjE2MzM0MjYxNzUsImlhdCI6MTYyODI0MjE3NSwianRpIjoiNTBjZjliNGYtYzRmZC00ZmMyLWEyYzMtMjRiMWZhNzJkOThkIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLXN0YWdlLm5vdXNkaWdpdGFsLm5ldC9hdXRoL3JlYWxtcy9zbWFlayIsInN1YiI6IjJmMzc5MDAwLWFmNGEtNGViNC1iMmQ1LWM5NzMyNmM2ZTM3YyIsInR5cCI6IkJlYXJlciIsImF6cCI6Imd1aWRlIiwic2Vzc2lvbl9zdGF0ZSI6IjkwZTc0ODE1LTgxZGItNDJhZS04MzNmLWQ3YmEyYjhiOWYxZCIsImFjciI6IjEiLCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIiwiY2xpZW50SWQiOiJndWlkZSIsImNsaWVudEhvc3QiOiI4NC4xMTQuODEuMTc3IiwicGVybWlzc2lvbnMiOlsibm91cy5jbXMuc2VydmVyLmNvbmZpZy5yZWFkIiwibm91cy5jbXMuZmlsZS5yZWFkIiwibm91cy5jbXMudG91ci5yZWFkIiwibm91cy5jbXMuZ3JvdXAucmVhZCIsIm5vdXMuY21zLm1hcC5yZWFkIiwibm91cy5jbXMuY29udGVudC5yZWFkIiwibm91cy5jbXMuZXhoaWJpdGlvbi5yZWFkIiwibm91cy5jbXMuaG90c3BvdC5yZWFkIiwibm91cy5jbXMuc3RhdGlvbi5yZWFkIiwibm91cy5jbXMuY2hhbm5lbC5yZWFkIiwibm91cy5zeW5jLnJlYWQiLCJub3VzLnN5bmMuY2hhbm5lbC5yZWFkIl0sInRlbmFudElkIjoiNjk2MmNlYzAtOTgyZC00YmQ2LWI2YTAtZGU5NjEwMGIwZWQwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWd1aWRlIiwiY2xpZW50QWRkcmVzcyI6Ijg0LjExNC44MS4xNzcifQ.JjECg-j-FiY_Uk1RbW17zFGvXy8oz9ntvKrq0VrCuTk99TTb-jny73yV6F6hbJUb6F6pLaJjzzlgELiGid9dW9mN8EVUUncMB635sG89PflikYqP3v9cyHk543UqRFmG_Hpqwknji1CZ2Uajtz8hVRuiIkw3iEQ6MIgNbnLHofyHI1wNETvm0Yahe1J_AtKBSDZRZJj5HVtcwHpty92B_hMRxFSX9v42EMiCaeyVSRLkbunCtt_6JOzoAVnNgJxnNiV1pgbRqoS-V4z0a68BW7WOj7C7Q4yHCyaOZbkWPILiF1pozCzeEdHKFxT5JRVezXKUJYPbTI5UbXnlQL7zWg";

    }

    private static  String createJWT(String subject, String issuer, String keyBase64Encoded, String tenant ) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyBase64Encoded));
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setHeaderParam("typ","JWT")
                .claim("unique_name", subject)
                .claim("exp", OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).toInstant().toEpochMilli());
        if(tenant != null){
            builder.claim("tenantId", tenant);
        }
        return builder.signWith(key).compact();
    }
}
