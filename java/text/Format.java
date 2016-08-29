package java.text;

import java.io.Serializable;

/**
 * 所有格式化的父类
 * 子类DateFormat、MessageFormat、NumberFormat
 */
public abstract class Format implements Serializable, Cloneable {

    private static final long serialVersionUID = -299282585814624189L;

    protected Format() {
    }

    /**
     * 格式化一个对象，返回string
     */
    public final String format (Object obj) {
        return format(obj, new StringBuffer(), new FieldPosition(0)).toString();
    }

    /**
     * 对obj进行格式化，返回一二StringBilder结果
     * 必须在子类中实现
     */
    public abstract StringBuffer format(Object obj,
                    StringBuffer toAppendTo,
                    FieldPosition pos);

    /**
     * Formats an Object producing an <code>AttributedCharacterIterator</code>.
     * You can use the returned <code>AttributedCharacterIterator</code>
     * to build the resulting String, as well as to determine information
     * about the resulting String.
     * <p>
     * Each attribute key of the AttributedCharacterIterator will be of type
     * <code>Field</code>. It is up to each <code>Format</code> implementation
     * to define what the legal values are for each attribute in the
     * <code>AttributedCharacterIterator</code>, but typically the attribute
     * key is also used as the attribute value.
     * <p>The default implementation creates an
     * <code>AttributedCharacterIterator</code> with no attributes. Subclasses
     * that support fields should override this and create an
     * <code>AttributedCharacterIterator</code> with meaningful attributes.
     *
     * @exception NullPointerException if obj is null.
     * @exception IllegalArgumentException when the Format cannot format the
     *            given object.
     * @param obj The object to format
     * @return AttributedCharacterIterator describing the formatted value.
     * @since 1.4
     */
    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return createAttributedCharacterIterator(format(obj));
    }

    /**
     * 将给出的字符串转换为Object
     * 子类中实现
     */
    public abstract Object parseObject (String source, ParsePosition pos);

    /**
     * Parses text from the beginning of the given string to produce an object.
     * The method may not use the entire text of the given string.
     *
     * @param source A <code>String</code> whose beginning should be parsed.
     * @return An <code>Object</code> parsed from the string.
     * @exception ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public Object parseObject(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        Object result = parseObject(source, pos);
        if (pos.index == 0) {
            throw new ParseException("Format.parseObject(String) failed",
                pos.errorIndex);
        }
        return result;
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // will never happen
            return null;
        }
    }

    //
    // Convenience methods for creating AttributedCharacterIterators from
    // different parameters.
    //

    /**
     * Creates an <code>AttributedCharacterIterator</code> for the String
     * <code>s</code>.
     *
     * @param s String to create AttributedCharacterIterator from
     * @return AttributedCharacterIterator wrapping s
     */
    AttributedCharacterIterator createAttributedCharacterIterator(String s) {
        AttributedString as = new AttributedString(s);

        return as.getIterator();
    }

    /**
     * Creates an <code>AttributedCharacterIterator</code> containg the
     * concatenated contents of the passed in
     * <code>AttributedCharacterIterator</code>s.
     *
     * @param iterators AttributedCharacterIterators used to create resulting
     *                  AttributedCharacterIterators
     * @return AttributedCharacterIterator wrapping passed in
     *         AttributedCharacterIterators
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
                       AttributedCharacterIterator[] iterators) {
        AttributedString as = new AttributedString(iterators);

        return as.getIterator();
    }

    /**
     * Returns an AttributedCharacterIterator with the String
     * <code>string</code> and additional key/value pair <code>key</code>,
     * <code>value</code>.
     *
     * @param string String to create AttributedCharacterIterator from
     * @param key Key for AttributedCharacterIterator
     * @param value Value associated with key in AttributedCharacterIterator
     * @return AttributedCharacterIterator wrapping args
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
                      String string, AttributedCharacterIterator.Attribute key,
                      Object value) {
        AttributedString as = new AttributedString(string);

        as.addAttribute(key, value);
        return as.getIterator();
    }

    /**
     * Creates an AttributedCharacterIterator with the contents of
     * <code>iterator</code> and the additional attribute <code>key</code>
     * <code>value</code>.
     *
     * @param iterator Initial AttributedCharacterIterator to add arg to
     * @param key Key for AttributedCharacterIterator
     * @param value Value associated with key in AttributedCharacterIterator
     * @return AttributedCharacterIterator wrapping args
     */
    AttributedCharacterIterator createAttributedCharacterIterator(
              AttributedCharacterIterator iterator,
              AttributedCharacterIterator.Attribute key, Object value) {
        AttributedString as = new AttributedString(iterator);

        as.addAttribute(key, value);
        return as.getIterator();
    }


    /**
     * Defines constants that are used as attribute keys in the
     * <code>AttributedCharacterIterator</code> returned
     * from <code>Format.formatToCharacterIterator</code> and as
     * field identifiers in <code>FieldPosition</code>.
     *
     * @since 1.4
     */
    public static class Field extends AttributedCharacterIterator.Attribute {

        // Proclaim serial compatibility with 1.4 FCS
        private static final long serialVersionUID = 276966692217360283L;

        /**
         * Creates a Field with the specified name.
         *
         * @param name Name of the attribute
         */
        protected Field(String name) {
            super(name);
        }
    }


    /**
     * FieldDelegate is notified by the various <code>Format</code>
     * implementations as they are formatting the Objects. This allows for
     * storage of the individual sections of the formatted String for
     * later use, such as in a <code>FieldPosition</code> or for an
     * <code>AttributedCharacterIterator</code>.
     * <p>
     * Delegates should NOT assume that the <code>Format</code> will notify
     * the delegate of fields in any particular order.
     *
     * @see FieldPosition.Delegate
     * @see CharacterIteratorFieldDelegate
     */
    interface FieldDelegate {
        /**
         * Notified when a particular region of the String is formatted. This
         * method will be invoked if there is no corresponding integer field id
         * matching <code>attr</code>.
         *
         * @param attr Identifies the field matched
         * @param value Value associated with the field
         * @param start Beginning location of the field, will be >= 0
         * @param end End of the field, will be >= start and <= buffer.length()
         * @param buffer Contains current formatted value, receiver should
         *        NOT modify it.
         */
        public void formatted(Format.Field attr, Object value, int start,
                              int end, StringBuffer buffer);

        /**
         * Notified when a particular region of the String is formatted.
         *
         * @param fieldID Identifies the field by integer
         * @param attr Identifies the field matched
         * @param value Value associated with the field
         * @param start Beginning location of the field, will be >= 0
         * @param end End of the field, will be >= start and <= buffer.length()
         * @param buffer Contains current formatted value, receiver should
         *        NOT modify it.
         */
        public void formatted(int fieldID, Format.Field attr, Object value,
                              int start, int end, StringBuffer buffer);
    }
}
