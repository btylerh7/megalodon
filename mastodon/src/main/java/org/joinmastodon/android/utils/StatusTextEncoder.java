package org.joinmastodon.android.utils;

import android.text.TextUtils;

import org.joinmastodon.android.fragments.ComposeFragment;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// not a good class
public class StatusTextEncoder {
	private final Function<String, String> fn;

	// see ComposeFragment.HIGHLIGHT_PATTERN
	private final static Pattern EXCLUDE_PATTERN = Pattern.compile("\\s*(?:@([a-zA-Z0-9_]+)(@[a-zA-Z0-9_.-]+)?|#([^\\s.]+))\\s*");

	public StatusTextEncoder(Function<String, String> fn) {
		this.fn = fn;
	}

	// prettiest method award winner 2023 [citation needed]
	public String encode(String content) {
		StringBuilder encodedString = new StringBuilder();
		// matches mentions and hashtags
		Matcher m = EXCLUDE_PATTERN.matcher(content);
		int previousEnd = 0;
		while (m.find()) {
			MatchResult res = m.toMatchResult();
			// everything before the match - do encode
			encodedString.append(fn.apply(content.substring(previousEnd, res.start())));
			previousEnd = res.end();
			// the match - do not encode
			encodedString.append(res.group());
		}
		// everything after the last match - do encode
		encodedString.append(fn.apply(content.substring(previousEnd)));
		return encodedString.toString();
	}

	// prettiest almost-exact replica of a pretty function
	public String decode(String content, Pattern regex) {
		Matcher m = regex.matcher(content);
		StringBuilder decodedString = new StringBuilder();
		int previousEnd = 0;
		while (m.find()) {
			MatchResult res = m.toMatchResult();
			// everything before the match - do not decode
			decodedString.append(content.substring(previousEnd, res.start()));
			previousEnd = res.end();
			// the match - do decode
			decodedString.append(fn.apply(res.group()));
		}
		decodedString.append(content.substring(previousEnd));
		return decodedString.toString();
	}
}
