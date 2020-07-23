package net.thisptr.jmx.exporter.agent.utils;

public class MoreClasses {

	/**
	 * Extracts the element type of the given type.
	 * 
	 * @param type a string representation of an array type. The format is the same as {@link Class#getName()} and thus must start with "[".
	 * @return a string representation of the element type. The format is the same as {@link Class#getName()}.
	 */
	public static String elementTypeNameOf(final String type) {
		if (type.length() < 2 || type.charAt(0) != '[')
			throw new IllegalArgumentException(type + " is not a valid array type");
		switch (type.charAt(1)) {
		case 'L':
			return type.substring(2, type.length() - 1);
		case 'Z':
			return "boolean";
		case 'B':
			return "byte";
		case 'C':
			return "char";
		case 'D':
			return "double";
		case 'F':
			return "float";
		case 'I':
			return "int";
		case 'J':
			return "long";
		case 'S':
			return "short";
		case '[':
			return type.substring(1);
		default:
			throw new IllegalArgumentException("cannot extract an element type from of " + type);
		}
	}

}
