package io.vertx.ext.sync.npe.service.user;

import java.util.List;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface UserDao
{
	static final String SERVICE_ADDRESS = "dao.user";
	
	static UserDao create(Vertx vertx, JsonObject config) 
	{
		return new UserDaoImpl(vertx, config);
	}

	static UserDao createProxy(Vertx vertx, String address) 
	{
		return new UserDaoVertxEBProxy(vertx, address);
	}
	
	void findAll(Handler<AsyncResult<List<JsonObject>>> result);
	
	void loadById(String id, Handler<AsyncResult<JsonObject>> result);
	
	@ProxyIgnore
	void close();
}
