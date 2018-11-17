package io.vertx.ext.sync.npe.model;

import java.io.Serializable;

/**
 * User of the application.
 */
public class User implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String familyName;

	private String givenName;
	
	private String email;
	
	public String getId() 
	{
		return id;
	}

	public void setId(String id) 
	{
		this.id = id;
	}

	public String getFamilyName()
	{
		return familyName;
	}

	public void setFamilyName(String familyName)
	{
		this.familyName = familyName;
	}

	public String getGivenName()
	{
		return givenName;
	}

	public void setGivenName(String givenName)
	{
		this.givenName = givenName;
	}
	
	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	@Override
	public String toString() 
	{
		return "User [id=" + id + ", familyName=" + familyName + ", givenName=" + givenName + ", email=" + email + "]";
	}
}
