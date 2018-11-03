package com.android.volley.toolbox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.volley.AuthFailureError;

@SuppressLint({"MissingPermission"})
public class AndroidAuthenticator
  implements Authenticator
{
  private final AccountManager a;
  private final Account b;
  private final String c;
  private final boolean d;
  
  public AndroidAuthenticator(Context paramContext, Account paramAccount, String paramString)
  {
    this(paramContext, paramAccount, paramString, false);
  }
  
  public AndroidAuthenticator(Context paramContext, Account paramAccount, String paramString, boolean paramBoolean)
  {
    this(AccountManager.get(paramContext), paramAccount, paramString, paramBoolean);
  }
  
  AndroidAuthenticator(AccountManager paramAccountManager, Account paramAccount, String paramString, boolean paramBoolean)
  {
    a = paramAccountManager;
    b = paramAccount;
    c = paramString;
    d = paramBoolean;
  }
  
  public Account getAccount()
  {
    return b;
  }
  
  public String getAuthTokenType()
  {
    return c;
  }
  
  public String getAuthToken()
    throws AuthFailureError
  {
    AccountManagerFuture localAccountManagerFuture = a.getAuthToken(b, c, d, null, null);
    Bundle localBundle;
    try
    {
      localBundle = (Bundle)localAccountManagerFuture.getResult();
    }
    catch (Exception localException)
    {
      throw new AuthFailureError("Error while retrieving auth token", localException);
    }
    String str = null;
    if ((localAccountManagerFuture.isDone()) && (!localAccountManagerFuture.isCancelled()))
    {
      if (localBundle.containsKey("intent"))
      {
        Intent localIntent = (Intent)localBundle.getParcelable("intent");
        throw new AuthFailureError(localIntent);
      }
      str = localBundle.getString("authtoken");
    }
    if (str == null) {
      throw new AuthFailureError("Got null auth token for type: " + c);
    }
    return str;
  }
  
  public void invalidateAuthToken(String paramString)
  {
    a.invalidateAuthToken(b.type, paramString);
  }
}
