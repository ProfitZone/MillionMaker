package com.million.sample;

import com.million.kite.login.TokenManager;
import com.million.kite.login.TokenType;
import com.neovisionaries.ws.client.WebSocketException;
import com.rainmatter.kiteconnect.KiteConnect;
import com.rainmatter.kitehttp.SessionExpiryHook;
import com.rainmatter.kitehttp.exceptions.KiteException;
import com.rainmatter.models.UserModel;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sujith on 7/10/16.
 * This class has example of how to initialize kiteSdk and make rest api calls to place order, get orders, modify order, cancel order,
 * get positions, get holdings, convert positions, get instruments, logout user, get historical data dump, get trades
 */
public class Test {

    public static void main(String[] args) throws FileNotFoundException, IOException{
        try {
                // First you should get request_token, public_token using kitconnect login and then use request_token, public_token, api_secret to make any kiteConnect api call.
                // Initialize KiteSdk with your apiKey.
                KiteConnect kiteConnect = new KiteConnect("byxwwjusrsbt88yi");

                // set userId
                kiteConnect.setUserId("SJ0899");

                // Get login url
                String url = kiteConnect.getLoginUrl();

                System.out.println(url);
                // Set session expiry callback.
                kiteConnect.registerHook(new SessionExpiryHook() {
                    @Override
                    public void sessionExpired() {
                        System.out.println("session expired");
                    }
                });

                //Set request token and public token which are obtained from login process.
                //UserModel userModel =  kiteConnect.requestAccessToken("hnqc1skz6mfoyja20cjh39pnl7yow69n", "9dyz61bypop6g9ptkvttna6cr1m9v6v2");
                
               // System.out.println(userModel.accessToken);
                //System.out.println(userModel.publicToken);
                
                TokenManager tokenManager = new TokenManager();
                
                kiteConnect.setAccessToken(tokenManager.getToken(TokenType.ACCESS));
                kiteConnect.setPublicToken(tokenManager.getToken(TokenType.PUBLIC));

                Examples examples = new Examples();

                /*
                 examples.getMargins(kiteConnect);

                examples.placeOrder(kiteConnect);

                examples.placeBracketOrder(kiteConnect);

                examples.modifyFirstLegBo(kiteConnect);

                examples.getTriggerRange(kiteConnect);

                examples.placeCoverOrder(kiteConnect);

                examples.getOrders(kiteConnect);

                examples.getOrder(kiteConnect);

                examples.getTrades(kiteConnect);

                examples.getTradesWithOrderId(kiteConnect);

                examples.modifyOrder(kiteConnect);

                examples.modifySecondLegBoSLM(kiteConnect);

                examples.modifySecondLegBoLIMIT(kiteConnect);

                examples.cancelOrder(kiteConnect);

                examples.exitBracketOrder(kiteConnect);

                examples.getPositions(kiteConnect);

                examples.getHoldings(kiteConnect);

                examples.modifyProduct(kiteConnect);

                examples.getAllInstruments(kiteConnect);

                examples.getInstrumentsForExchange(kiteConnect);

                examples.getQuote(kiteConnect);

                examples.getHistoricalData(kiteConnect);

                examples.getOHLC(kiteConnect); */

                examples.getLTP(kiteConnect);

                /*
                examples.getMfInstruments(kiteConnect);

                examples.placeMfOrder(kiteConnect);

                examples.cancelMfOrder(kiteConnect);

                examples.getMfOrders(kiteConnect);

                examples.getMfOrder(kiteConnect);

                examples.placeMfSip(kiteConnect);

                examples.modifyMfSip(kiteConnect);

                examples.cancelMfSip(kiteConnect);

                examples.getMfSips(kiteConnect);

                examples.getMfSip(kiteConnect);

                examples.getMfHoldings(kiteConnect);

                examples.logout(kiteConnect);

                ArrayList<Long> tokens = new ArrayList<>();
                tokens.add(Long.parseLong("256265"));
                tokens.add(Long.parseLong("265"));
                examples.tickerUsage(kiteConnect, tokens);
                
                */
        } catch (KiteException e) {
            System.out.println(e.message+" "+e.code+" "+e.getClass().getName());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
