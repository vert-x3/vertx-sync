package io.vertx.ext.sync.poem;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.sync.SyncVerticle;

public class PoemVerticleForTesting extends SyncVerticle 
{
	/**
	 * Returns the Rimbaud's poem called : "Le dormeur du val" in using the vertx sync API.
	 * 
	 * @return content poem of Rimbaud
	 */
	@Suspendable
	public String loadTheDormeurDuValPoem()
	{
		final String result;
		final boolean exists = Sync.awaitResult(h -> vertx.fileSystem().exists("./le_dormeur_du_val.txt", h));
		
		if (exists)
		{
			final Buffer buf = Sync.awaitResult(h -> vertx.fileSystem().readFile("./le_dormeur_du_val.txt", h));
			result = buf.toString();
		}
		else
		{
			result = "";
		}
		
		return result;
	}
}
