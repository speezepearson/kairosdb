package org.kairosdb.core.http.rest.json;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.kairosdb.core.datastore.SetValuedTagPredicate;
import org.kairosdb.core.datastore.SimpleSetValuedTagPredicate;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetValuedTagPredicateDeserializer implements JsonDeserializer<SetValuedTagPredicate>
{
	@Override
	public SetValuedTagPredicate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		Set<ImmutableSet<String>> containsAlls = Sets.newHashSet();
		Set<ImmutableSet<String>> containsAnys = Sets.newHashSet();
		Set<ImmutableSet<String>> exactlys = Sets.newHashSet();
		JsonObject query = json.getAsJsonObject();
		for (JsonElement jPredicate : query.get("all_true").getAsJsonArray())
		{
			JsonObject predicate = jPredicate.getAsJsonObject();
			if (predicate.entrySet().size() != 1) { throw new RuntimeException(); }

			Map.Entry<String, JsonElement> entry = predicate.entrySet().iterator().next();
			switch (entry.getKey())
			{
				case "contains_all":
					containsAlls.add(parseStringSet(entry.getValue().getAsJsonArray()));
					break;
				case "contains_any":
					containsAnys.add(parseStringSet(entry.getValue().getAsJsonArray()));
					break;
				case "exactly":
					exactlys.add(parseStringSet(entry.getValue().getAsJsonArray()));
					break;
				default:
					throw new RuntimeException();
			}
		}

		return new SimpleSetValuedTagPredicate(
				ImmutableSet.copyOf(containsAlls),
				ImmutableSet.copyOf(containsAnys),
				ImmutableSet.copyOf(exactlys));
	}

	private static ImmutableSet<String> parseStringSet(JsonArray json) throws JsonParseException
	{
		Set<String> result = Sets.newHashSet();
		for (JsonElement e : json)
		{
			result.add(e.getAsString());
		}
		return ImmutableSet.copyOf(result);
	}
}
