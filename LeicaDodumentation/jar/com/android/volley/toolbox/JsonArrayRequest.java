package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import java.io.UnsupportedEncodingException;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonArrayRequest
  extends JsonRequest<JSONArray>
{
  public JsonArrayRequest(String paramString, Response.Listener<JSONArray> paramListener, Response.ErrorListener paramErrorListener)
  {
    super(0, paramString, null, paramListener, paramErrorListener);
  }
  
  public JsonArrayRequest(int paramInt, String paramString, JSONArray paramJSONArray, Response.Listener<JSONArray> paramListener, Response.ErrorListener paramErrorListener)
  {
    super(paramInt, paramString, paramJSONArray == null ? null : paramJSONArray.toString(), paramListener, paramErrorListener);
  }
  
  protected Response<JSONArray> parseNetworkResponse(NetworkResponse paramNetworkResponse)
  {
    try
    {
      String str = new String(data, HttpHeaderParser.parseCharset(headers, "utf-8"));
      return Response.success(new JSONArray(str), HttpHeaderParser.parseCacheHeaders(paramNetworkResponse));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      return Response.error(new ParseError(localUnsupportedEncodingException));
    }
    catch (JSONException localJSONException)
    {
      return Response.error(new ParseError(localJSONException));
    }
  }
}
