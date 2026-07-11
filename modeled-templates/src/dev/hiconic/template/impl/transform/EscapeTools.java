package dev.hiconic.template.impl.transform;

import java.nio.charset.StandardCharsets;

final class EscapeTools {
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	private EscapeTools() {
	}

	static String html(String input) {
		StringBuilder result = new StringBuilder(input.length() + 16);
		input.codePoints().forEach(cp -> {
			switch (cp) {
			case '&':
				result.append("&amp;");
				break;
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&#39;");
				break;
			default:
				result.appendCodePoint(validXmlCodePoint(cp) ? cp : 0xfffd);
			}
		});
		return result.toString();
	}

	static String xml(String input) {
		StringBuilder result = new StringBuilder(input.length() + 16);
		input.codePoints().forEach(cp -> {
			switch (cp) {
			case '&':
				result.append("&amp;");
				break;
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&apos;");
				break;
			default:
				result.appendCodePoint(validXmlCodePoint(cp) ? cp : 0xfffd);
			}
		});
		return result.toString();
	}

	static String javaLiteral(String input) {
		return quotedLanguage(input, false);
	}

	static String javaScript(String input) {
		return quotedLanguage(input, true);
	}

	static String json(String input) {
		StringBuilder result = new StringBuilder(input.length() + 16);
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			switch (ch) {
			case '"':
				result.append("\\\"");
				break;
			case '\\':
				result.append("\\\\");
				break;
			case '\b':
				result.append("\\b");
				break;
			case '\f':
				result.append("\\f");
				break;
			case '\n':
				result.append("\\n");
				break;
			case '\r':
				result.append("\\r");
				break;
			case '\t':
				result.append("\\t");
				break;
			default:
				if (ch < 0x20 || ch == '\u2028' || ch == '\u2029')
					unicode(result, ch);
				else
					result.append(ch);
			}
		}
		return result.toString();
	}

	static String css(String input) {
		StringBuilder result = new StringBuilder(input.length() + 16);
		input.codePoints().forEach(cp -> {
			if (isAsciiAlphaNumeric(cp) || cp == '-' || cp == '_')
				result.appendCodePoint(cp);
			else {
				result.append('\\').append(Integer.toHexString(cp).toUpperCase()).append(' ');
			}
		});
		return result.toString();
	}

	static String urlComponent(String input) {
		byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
		StringBuilder result = new StringBuilder(bytes.length * 3);
		for (byte value : bytes) {
			int b = value & 0xff;
			if (isAsciiAlphaNumeric(b) || b == '-' || b == '.' || b == '_' || b == '~')
				result.append((char) b);
			else
				result.append('%').append(HEX[b >>> 4]).append(HEX[b & 0xf]);
		}
		return result.toString();
	}

	private static String quotedLanguage(String input, boolean scriptSafe) {
		StringBuilder result = new StringBuilder(input.length() + 16);
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			switch (ch) {
			case '"':
				result.append("\\\"");
				break;
			case '\'':
				result.append("\\'");
				break;
			case '\\':
				result.append("\\\\");
				break;
			case '\b':
				result.append("\\b");
				break;
			case '\f':
				result.append("\\f");
				break;
			case '\n':
				result.append("\\n");
				break;
			case '\r':
				result.append("\\r");
				break;
			case '\t':
				result.append("\\t");
				break;
			default:
				if (ch < 0x20 || ch == '\u2028' || ch == '\u2029'
						|| scriptSafe && (ch == '<' || ch == '>' || ch == '&'))
					unicode(result, ch);
				else
					result.append(ch);
			}
		}
		return result.toString();
	}

	private static void unicode(StringBuilder result, char ch) {
		result.append("\\u")
				.append(HEX[ch >>> 12])
				.append(HEX[ch >>> 8 & 0xf])
				.append(HEX[ch >>> 4 & 0xf])
				.append(HEX[ch & 0xf]);
	}

	private static boolean validXmlCodePoint(int cp) {
		return cp == 0x9 || cp == 0xa || cp == 0xd
				|| cp >= 0x20 && cp <= 0xd7ff
				|| cp >= 0xe000 && cp <= 0xfffd
				|| cp >= 0x10000 && cp <= 0x10ffff;
	}

	private static boolean isAsciiAlphaNumeric(int cp) {
		return cp >= 'a' && cp <= 'z' || cp >= 'A' && cp <= 'Z' || cp >= '0' && cp <= '9';
	}
}
