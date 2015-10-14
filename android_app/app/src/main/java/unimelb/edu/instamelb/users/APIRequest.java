package unimelb.edu.instamelb.users;


import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import unimelb.edu.instamelb.extras.Constants;

/**
 * Created by Kevin on 2015/9/9.
 */
public class APIRequest {
    private String mUsername;
    private String mPassword;

    public APIRequest(){
        mUsername="";
        mPassword="";
    }
    public APIRequest(String username,String password){
        mUsername=username;
        mPassword=password;
    }


    public String createRequest(String method, String endpoint, JSONObject params) throws Exception {
        if (method.equals("POSTPHOTO")){
            return requestPostPhoto(endpoint, params);
        }
        return null;
    }
    /**
     * Create http request to an instagram api endpoint.
     * This is a synchronus method, so you have to call it on a separate thread.
     *
     * @param method Http method, can be GET or POST
     * @param endpoint Api endpoint.
     * @param params Request parameters
     *
     * @return Api response in json format.
     *
     * @throws Exception If error occured.
     */
    public String createRequest(String method, String endpoint, List<NameValuePair> params) throws Exception {
        if (method.equals("POST")) {
            return requestPost(endpoint, params);
        }
        else if (method.equals("DELETE")){
            return requestDelete(endpoint, params);
        }else if (method.equals("GETQUERY")){
            return requestGetQuery(endpoint,params);
        }else{
            return requestGet(endpoint, params);
        }
    }

    private String requestGetQuery(String endpoint, List<NameValuePair> params) throws Exception {
        String requestUri = Constants.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);
        return getQuery(requestUri, params);
    }

    public String getQuery(String requestUri, List<NameValuePair> params) throws Exception {
        InputStream stream 	= null;
        String response		= "";

        try {
            String requestUrl = requestUri;

            if (params != null) {
                StringBuilder requestParamSb = new StringBuilder();
                int size = params.size();
                for (int i = 0; i < size; i++) {
                    BasicNameValuePair param = (BasicNameValuePair) params.get(i);

                    requestParamSb.append(param.getName() + "=" + param.getValue() + ((i != size-1) ? "&" : ""));
                }

                String requestParam  = requestParamSb.toString();
                requestUrl = requestUri + ((requestUri.contains("?")) ? "&" + requestParam : "?" + requestParam);
            }

            Log.i("GET " , requestUrl);
            byte[] pair= (mUsername + ":" + mPassword).getBytes();
            String encoding = Base64.encodeToString(pair,Base64.NO_WRAP);
            HttpClient httpClient     = new DefaultHttpClient();
            HttpGet httpGet       = new HttpGet(requestUrl);
            if (mUsername != ""){
                httpGet.setHeader("Authorization", "Basic " + encoding);
            }

            HttpResponse httpResponse   = httpClient.execute(httpGet);
            HttpEntity httpEntity     = httpResponse.getEntity();

            if (httpEntity == null) {
                throw new Exception("Request returns empty result");
            }

            stream    = httpEntity.getContent();
            response	= streamToString(stream);

            Log.i("Response ", response);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return response;
    }

    private String requestDelete(String endpoint, List<NameValuePair> params) throws Exception{
        String requestUri = Constants.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);
        return delete(requestUri,params);

    }



    /**
     * Create http request to an instagram api endpoint.
     * This is an asynchronous method, so you have to define a listener to handle the result.
     *  @param method Http method, can be GET or POST
     * @param endpoint Api endpoint.
     * @param params Request parameters
     * @param listener Request listener
     *
     */
    public void createRequest(String method, String endpoint, List<NameValuePair> params, APIRequestListener listener) {
        new RequestTask(method, endpoint, params, listener).execute();
    }

    /**
     * Create http GET request to an instagram api endpoint.
     *
     * @param endpoint Api endpoint.
     * @param params Request parameters
     *
     * @return Api response in json format.
     *
     * @throws Exception If error occured.
     */
    private String requestGet(String endpoint, List<NameValuePair> params) throws Exception {
        String requestUri = Constants.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);

        return get(requestUri, params);
    }

    /**
     * Create http POST request to an instagram api endpoint.
     *
     * @param endpoint Api endpoint.
     * @param params Request parameters
     *
     * @return Api response in json format.
     *
     * @throws Exception If error occured.
     */
    private String requestPost(String endpoint, List<NameValuePair> params) throws Exception {
        String requestUri = Constants.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);

        return post(requestUri, params);
    }
    private String requestPostPhoto(String endpoint, JSONObject params) throws Exception {
        String requestUri = Constants.API_BASE_URL + ((endpoint.indexOf("/") == 0) ? endpoint : "/" + endpoint);

        return postphoto(requestUri, params);
    }

    /**
     * Create http GET request to an instagram api endpoint.
     *
     * @param requestUri Api url
     * @param params Request parameters
     *
     * @return Api response in json format.
     *
     * @throws Exception If error occured.
     */
    public String get(String requestUri, List<NameValuePair> params) throws Exception {
        InputStream stream 	= null;
        String response		= "";

        try {
            String requestUrl = requestUri;

            if (params != null) {
                StringBuilder requestParamSb = new StringBuilder();
                int size = params.size();

                for (int i = 0; i < size; i++) {
                    BasicNameValuePair param = (BasicNameValuePair) params.get(i);

                    requestParamSb.append(param.getName() + "/" + param.getValue() + ((i != size-1) ? "/" : ""));
                }

                String requestParam	= requestParamSb.toString();
                requestUrl = requestUri + ((requestUri.endsWith("/")) ? "" + requestParam : "/" + requestParam);
            }

            Log.i("GET ",requestUrl);
            byte[] pair= (mUsername + ":" + mPassword).getBytes();
            String encoding = Base64.encodeToString(pair,Base64.NO_WRAP);
            HttpClient httpClient 		= new DefaultHttpClient();
            HttpGet httpGet 			= new HttpGet(requestUrl);
            if (mUsername != ""){
                httpGet.setHeader("Authorization", "Basic " + encoding);
            }

            HttpResponse httpResponse 	= httpClient.execute(httpGet);
            HttpEntity httpEntity 		= httpResponse.getEntity();

            if (httpEntity == null) {
                throw new Exception("Request returns empty result");
            }

            stream		= httpEntity.getContent();
            response	= streamToString(stream);

            Log.i("Response " , response);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return response;
    }

    private String delete(String requestUri, List<NameValuePair> params) throws Exception{
        InputStream stream 	= null;
        String response		= "";

        try {
            String requestUrl = requestUri;

            if (params != null) {
                StringBuilder requestParamSb = new StringBuilder();
                int size = params.size();

                for (int i = 0; i < size; i++) {
                    BasicNameValuePair param = (BasicNameValuePair) params.get(i);

                    requestParamSb.append(param.getName() + "/" + param.getValue() + ((i != size-1) ? "/" : ""));
                }

                String requestParam	= requestParamSb.toString();
                requestUrl = requestUri + ((requestUri.endsWith("/")) ? "" + requestParam : "/" + requestParam);
            }

            Log.i("DELETE ",requestUrl);
            byte[] pair= (mUsername + ":" + mPassword).getBytes();
            String encoding = Base64.encodeToString(pair, Base64.NO_WRAP);
            HttpClient httpClient 		= new DefaultHttpClient();
            HttpDelete httpDelete 			= new HttpDelete(requestUrl);
            if (mUsername != ""){
                httpDelete.setHeader("Authorization", "Basic " + encoding);
            }

            HttpResponse httpResponse 	= httpClient.execute(httpDelete);
            HttpEntity httpEntity 		= httpResponse.getEntity();

            if (httpEntity == null) {
                throw new Exception("Request returns empty result");
            }

            stream		= httpEntity.getContent();
            response	= streamToString(stream);

            Log.i("Response " , response);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return response;
    }



    public static String fromStream(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }


    /**
     * Create http POST request to an instagram api endpoint.
     *
     * @param requestUrl Api url
     * @param params Request parameters
     *
     * @return Api response in json format.
     *
     * @throws Exception If error occured.
     */
    public String post(String requestUrl, List<NameValuePair> params) throws Exception {
        InputStream stream 	= null;
        String response		= "";

        try {
            Log.d(mUsername,mPassword);
            byte[] pair= (mUsername + ":" + mPassword).getBytes();
            String encoding = Base64.encodeToString(pair,Base64.NO_WRAP);
            HttpClient httpClient 	= new DefaultHttpClient();
            HttpPost httpPost 		= new HttpPost(requestUrl);
            if (mUsername != ""){
                httpPost.setHeader("Authorization", "Basic " + encoding);
            }
            //httpPost.setHeader("Content-Type", "application/json");
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
            //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse 	= httpClient.execute(httpPost);
            HttpEntity httpEntity 		= httpResponse.getEntity();

            if (httpEntity == null) {
                throw new Exception("Request returns empty result");
            }

            stream		= httpEntity.getContent();
            response	= streamToString(stream);

            Log.i("Response " , response);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            throw e;
        }

        return response;
    }

    public String postphoto(String requestUrl, JSONObject params) throws Exception {
        InputStream stream 	= null;
        String response		= "";

        try {
            Log.d(mUsername,mPassword);
            byte[] pair= (mUsername + ":" + mPassword).getBytes();
            String encoding = Base64.encodeToString(pair,Base64.NO_WRAP);
            HttpClient httpClient 	= new DefaultHttpClient();
            HttpPost httpPost 		= new HttpPost(requestUrl);
            if (mUsername != ""){
                httpPost.setHeader("Authorization", "Basic " + encoding);
            }
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(params.toString());
            //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(entity);
            HttpResponse httpResponse 	= httpClient.execute(httpPost);
            HttpEntity httpEntity 		= httpResponse.getEntity();

            if (httpEntity == null) {
                throw new Exception("Request returns empty result");
            }

            stream		= httpEntity.getContent();
            response	= streamToString(stream);

            Log.i("Response " , response);

            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
            }
        } catch (Exception e) {
            throw e;
        }

        return response;
    }

    private class RequestTask extends AsyncTask<URL, Integer, Long> {
        String method, endpoint, response = "";

        List<NameValuePair> params;

        APIRequestListener listener;

        public RequestTask(String method, String endpoint,  List<NameValuePair> params, APIRequestListener listener) {
            this.method		= method;
            this.endpoint	= endpoint;
            this.params		= params;
            this.listener 	= listener;
        }

        protected void onCancelled() {
        }

        protected void onPreExecute() {
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                if (method.equals("POST")) {
                    response = requestPost(endpoint, params);
                } else {
                    response = requestGet(endpoint, params);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            if (!response.equals("")) {
                listener.onSuccess(response);
            } else {
                listener.onError("Failed to process api request");
            }
        }
    }

    //Request listener
    public interface APIRequestListener {
        public abstract void onSuccess(String response);
        public abstract void onError(String error);
    }
    public static String streamToString(InputStream is) throws IOException {
        String str  = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader 	= new BufferedReader(new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }
}

