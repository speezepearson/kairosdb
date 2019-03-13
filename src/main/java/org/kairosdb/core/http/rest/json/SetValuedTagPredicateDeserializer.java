package org.kairosdb.core.http.rest.json;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.kairosdb.core.datastore.setvaluedtags.ContainsAnyPredicate;
import org.kairosdb.core.datastore.setvaluedtags.ExactlyPredicate;
import org.kairosdb.core.datastore.setvaluedtags.SetValuedTagPredicate;
import org.kairosdb.core.datastore.setvaluedtags.ContainsAllPredicate;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SetValuedTagPredicateDeserializer implements JsonDeserializer<SetValuedTagPredicate>
{

	class InvalidPredicateException extends IllegalArgumentException {
		InvalidPredicateException(String message) { super(message); }
	}

	@Override
	public SetValuedTagPredicate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject predicate = json.getAsJsonObject();
		if (predicate.entrySet().size() != 1) {
			Set<String> keys = predicate.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
			throw new InvalidPredicateException("predicate must have exactly 1 key; got " + keys);
		}

		Map.Entry<String, JsonElement> entry = predicate.entrySet().iterator().next();
		switch (entry.getKey())
		{
			case "contains_all":
				return new ContainsAllPredicate(parseStringSet(entry.getValue().getAsJsonArray()));
			case "contains_any":
				return new ContainsAnyPredicate(parseStringSet(entry.getValue().getAsJsonArray()));
			case "exactly":
				return new ExactlyPredicate(parseStringSet(entry.getValue().getAsJsonArray()));
			default:
				throw new InvalidPredicateException("invalid predicate key: " + entry.getKey());
		}
	}

	private static ImmutableSortedSet<String> parseStringSet(JsonArray json) throws JsonParseException
	{
		Set<String> result = Sets.newHashSet();
		for (JsonElement e : json)
		{
			result.add(e.getAsString());
		}
		return ImmutableSortedSet.copyOf(result);
	}
}
