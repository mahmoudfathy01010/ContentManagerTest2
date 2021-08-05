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
        return "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLZHZQZnEwdzFUUEpnbkpKVThZZXppYThkUG8yVkhBQWV0SXBDMGN3Yl9rIn0.eyJleHAiOjE2MjgxODM5MDMsImlhdCI6MTYyODE4MzYwMywianRpIjoiMjFhNjUxMzQtNDg4Ny00YzZjLTk1ZWMtOTIyYjEwOTlmMmI1IiwiaXNzIjoiaHR0cHM6Ly9hdXRoLXN0YWdlLm5vdXNkaWdpdGFsLm5ldC9hdXRoL3JlYWxtcy9zbWFlayIsInN1YiI6IjE3MmI1Mjk5LTMwNjAtNGQ2NC1iM2VkLTBlYzQ5MGE1NjM4OSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNtcyIsInNlc3Npb25fc3RhdGUiOiJiZWYyOGYzMi04MGExLTRkZWUtOTVmZC1hZTYzNTJhMGQ4MTMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicGVybWlzc2lvbnMiOlsibm91cy50cmFuc2xhdGlvbi5yZWFkIiwibm91cy5zeW5jLnJlYWQiLCJub3VzLnN5bmMuY2hhbm5lbC5yZWFkIiwibm91cy5jbXMuZmlsZS5yZWFkIiwibm91cy5jbXMudG91ci5yZWFkIiwibm91cy5jbXMubWFwLnJlYWQiLCJub3VzLmNtcy5jb250ZW50LnJlYWQiLCJub3VzLmNtcy5leGhpYml0aW9uLnJlYWQiLCJub3VzLmNtcy5zdGF0aW9uLnJlYWQiXSwidGVuYW50SWQiOiI2OTYyY2VjMC05ODJkLTRiZDYtYjZhMC1kZTk2MTAwYjBlZDAiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhcHAiLCJ1c2VybmFtZSI6ImFwcCJ9.L3JUsDX0EX98ohsGfshG1Ekc4xgbjh-GT777NKSlgMKPjG7OCmLj3Jd-ElFwlroFdIZvZdDve4h-MWLRfokaYKKqEVh_EQWUFFcjfKOrsbzQnprDkNtVN_QkMDd0dsfNeNJMcDDpFYKb8ZnwFJXUDFhVAamNrXWbVXklBspjm-YrxjFMQQAs-ydWip_y3jJ30tprNaaFYZsZdfTVMsDY2Q2ALT9MzVhY90goWMSHkHrxyYJU21FPg2cG0kWugzKGAjluqqyTPusw2xt9XyBdv5w7uvOZBFL8mRCXIJaAVUm5nGDmiR4HJN4uLH7wxCWnoO5JWU9P5l9K2Xk_DJq0rw";
    }
    //TODO: GET CORRECT KEY
    public static String getJwtTokenForSyncServer(){
        return "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJLZHZQZnEwdzFUUEpnbkpKVThZZXppYThkUG8yVkhBQWV0SXBDMGN3Yl9rIn0.eyJleHAiOjE2MjgxODM5MDMsImlhdCI6MTYyODE4MzYwMywianRpIjoiMjFhNjUxMzQtNDg4Ny00YzZjLTk1ZWMtOTIyYjEwOTlmMmI1IiwiaXNzIjoiaHR0cHM6Ly9hdXRoLXN0YWdlLm5vdXNkaWdpdGFsLm5ldC9hdXRoL3JlYWxtcy9zbWFlayIsInN1YiI6IjE3MmI1Mjk5LTMwNjAtNGQ2NC1iM2VkLTBlYzQ5MGE1NjM4OSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNtcyIsInNlc3Npb25fc3RhdGUiOiJiZWYyOGYzMi04MGExLTRkZWUtOTVmZC1hZTYzNTJhMGQ4MTMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicGVybWlzc2lvbnMiOlsibm91cy50cmFuc2xhdGlvbi5yZWFkIiwibm91cy5zeW5jLnJlYWQiLCJub3VzLnN5bmMuY2hhbm5lbC5yZWFkIiwibm91cy5jbXMuZmlsZS5yZWFkIiwibm91cy5jbXMudG91ci5yZWFkIiwibm91cy5jbXMubWFwLnJlYWQiLCJub3VzLmNtcy5jb250ZW50LnJlYWQiLCJub3VzLmNtcy5leGhpYml0aW9uLnJlYWQiLCJub3VzLmNtcy5zdGF0aW9uLnJlYWQiXSwidGVuYW50SWQiOiI2OTYyY2VjMC05ODJkLTRiZDYtYjZhMC1kZTk2MTAwYjBlZDAiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhcHAiLCJ1c2VybmFtZSI6ImFwcCJ9.L3JUsDX0EX98ohsGfshG1Ekc4xgbjh-GT777NKSlgMKPjG7OCmLj3Jd-ElFwlroFdIZvZdDve4h-MWLRfokaYKKqEVh_EQWUFFcjfKOrsbzQnprDkNtVN_QkMDd0dsfNeNJMcDDpFYKb8ZnwFJXUDFhVAamNrXWbVXklBspjm-YrxjFMQQAs-ydWip_y3jJ30tprNaaFYZsZdfTVMsDY2Q2ALT9MzVhY90goWMSHkHrxyYJU21FPg2cG0kWugzKGAjluqqyTPusw2xt9XyBdv5w7uvOZBFL8mRCXIJaAVUm5nGDmiR4HJN4uLH7wxCWnoO5JWU9P5l9K2Xk_DJq0rw";

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
