package xml_analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * XMLTokener реализует методы для парсинга xml документа.
 * @author Данилов Вадислав ПИ-41
 */
public class XMLTokener{

   public static final java.util.HashMap<String, Character> entity;
   @SuppressWarnings("unused")
private long    character;
   private boolean eof;
   private long    index;
   @SuppressWarnings("unused")
private long    line;
   public char    previous;
   private Reader  reader;
   private boolean usePrevious;

   static {
       entity = new java.util.HashMap<String, Character>(8);
       entity.put("amp",  XML.AMP);
       entity.put("apos", XML.APOS);
       entity.put("gt",   XML.GT);
       entity.put("lt",   XML.LT);
       entity.put("quot", XML.QUOT);
   }
    
    /**
     * Конструктор XMLTokener для Reader.
     *
     * @param reader На вход подается объект Reader.
     */
    public XMLTokener(Reader reader) {
        this.reader = reader.markSupported()
            ? reader
            : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0;
        this.character = 1;
        this.line = 1;
    }

    /**
     * Получает текст в блоке CDATA.
     * @return Строка символов.
     */
    public String nextCDATA(){
        char         c;
        int          i;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            c = next();
            if (end()) {
            	System.out.print(" - Error: Unclosed CDATA \n");
            }
            sb.append(c);
            i = sb.length() - 3;
            if (i >= 0 && sb.charAt(i) == ']' &&
                          sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
                sb.setLength(i);
                return sb.toString();
            }
        }
    }


    /**
     * Осуществляет переходы между лексемами
     *
     * @return Лексема. Строка, знак, null.
     */
    public Object nextContent(){
        char         c;
        StringBuilder sb;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        if (c == 0) {
            return null;
        }
        if (c == '<') {
            return XML.LT;
        }
        sb = new StringBuilder();
        for (;;) {
            if (c == '<' || c == 0) {
                back();
                return sb.toString().trim();
            }
            if (c == '&') {
                sb.append(nextEntity(c));
            } else {
                sb.append(c);
            }
            c = next();
        }
    }


    /**
     * Данные значения перевоит в символы:
     *     <code>&amp;  &apos;  &gt;  &lt;  &quot;</code>.
     * @param ampersand Получает на вход символ амперсант.
     * @return  возвращает преобразованный элемент.
     */
    public Object nextEntity(char ampersand){
        StringBuilder sb = new StringBuilder();
        for (;;) {
            char c = next();
            if (Character.isLetterOrDigit(c) || c == '#') {
                sb.append(Character.toLowerCase(c));
            } else if (c == ';') {
                break;
            } else {
            	System.out.print(" - Error: Missing ';' in XML entity: &" + sb + "\n");
            }
        }
        String string = sb.toString();
        Object object = entity.get(string);
        return object != null ? object : ampersand + string + ";";
    }


    /**
     * Читает данные между символами <!...> и <?...?>.
     * @return возвращает значение boolean
     */
    public Object nextMeta(){
        char c;
        char q;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
        case 0:
        	System.out.print(" - Error: " + "Misshaped meta tag" + "\n");
        case '<':
            return XML.LT;
        case '>':
            return XML.GT;
        case '/':
            return XML.SLASH;
        case '=':
            return XML.EQ;
        case '!':
            return XML.BANG;
        case '?':
            return XML.QUEST;
        case '"':
        case '\'':
            q = c;
            for (;;) {
                c = next();
                if (c == 0) {
                	System.out.print(" - Error: " + "Unterminated string" + "\n");
                }
                if (c == q) {
                    return Boolean.TRUE;
                }
            }
        default:
            for (;;) {
                c = next();
                if (Character.isWhitespace(c)) {
                    return Boolean.TRUE;
                }
                switch (c) {
                case 0:
                case '<':
                case '>':
                case '/':
                case '=':
                case '!':
                case '?':
                case '"':
                case '\'':
                    back();
                    return Boolean.TRUE;
                }
            }
        }
    }


    /**
     * Возвращает лексемы.
     * @return строка или символ.
     */
    public Object nextToken(){
        char c;
        char q;
        StringBuilder sb;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
        case 0:
        	System.out.print(" - Error: " + "Misshaped element" + "\n");
        case '<':
        	System.out.print(" - Error: " + "Misplaced '<'" + "\n");
        case '>':
            return XML.GT;
        case '/':
            return XML.SLASH;
        case '=':
            return XML.EQ;
        case '!':
            return XML.BANG;
        case '?':
            return XML.QUEST;
        case '"':
        case '\'':
            q = c;
            sb = new StringBuilder();
            for (;;) {
                c = next();
                if (c == 0) {
                	System.out.print(" - Error: " + "Unterminated string" + "\n");
                }
                if (c == q) {
                    return sb.toString();
                }
                if (c == '&') {
                    sb.append(nextEntity(c));
                } else {
                    sb.append(c);
                }
            }
        default:
            sb = new StringBuilder();
            for (;;) {
                sb.append(c);
                c = next();
                if (Character.isWhitespace(c)) {
                    return sb.toString();
                }
                switch (c) {
                case 0:
                    return sb.toString();
                case '>':
                case '/':
                case '=':
                case '!':
                case '?':
                case '[':
                case ']':
                    back();
                    return sb.toString();
                case '<':
                case '"':
                case '\'':
                	System.out.print(" - Error: " + "Bad character in a name" + "\n");
                }
            }
        }
    }


    /**
     * Пропускает символ
     * @param возвращает boolean
     */
    public boolean skipPast(String to){
        boolean b;
        char c;
        int i;
        int j;
        int offset = 0;
        int length = to.length();
        char[] circle = new char[length];

        for (i = 0; i < length; i += 1) {
            c = next();
            if (c == 0) {
                return false;
            }
            circle[i] = c;
        }

        for (;;) {
            j = offset;
            b = true;

            for (i = 0; i < length; i += 1) {
                if (circle[j] != to.charAt(i)) {
                    b = false;
                    break;
                }
                j += 1;
                if (j >= length) {
                    j -= length;
                }
            }

            if (b) {
                return true;
            }

            c = next();
            if (c == 0) {
                return false;
            }
            circle[offset] = c;
            offset += 1;
            if (offset >= length) {
                offset -= length;
            }
        }
    }
    
    /**
     * Переходит по символам в строке.
     *
     * @return следующий символ
     */
    public char next(){
        int c = 0;
        if (this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
            	System.out.print(" - Error: " + exception + "\n");
            }

            if (c <= 0) {
                this.eof = true;
                c = 0;
            }
        }
        this.index += 1;
        if (this.previous == '\r') {
            this.line += 1;
            this.character = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            this.line += 1;
            this.character = 0;
        } else {
            this.character += 1;
        }
        this.previous = (char) c;
        return this.previous;
    }
    
     public String next(int n){
         if (n == 0) {
             return "";
         }

         char[] chars = new char[n];
         int pos = 0;

         while (pos < n) {
             chars[pos] = this.next();
             if (this.end()) {
            	 System.out.print(" - Error: " + "Substring bounds error" + "\n");
             }
             pos += 1;
         }
         return new String(chars);
     }
     
     public boolean end() {
         return this.eof && !this.usePrevious;
     }
     
     /**
      * возврат на один символ
      */
     public void back(){
         if (this.usePrevious || this.index <= 0) {
        	 System.out.print(" - Error: " + "Stepping back two steps is not supported" + "\n");
         }
         this.index -= 1;
         this.character -= 1;
         this.usePrevious = true;
         this.eof = false;
     }
     /**
      * проверяет есть ли еще символы в строке
      * @return true если не достигли конца.
      */
     public boolean more(){
         this.next();
         if (this.end()) {
             return false;
         }
         this.back();
         return true;
     }
     
     /**
      * Конструктор XMLTokener из строки.
      * @param строку для другого конструктора.
      */
     public XMLTokener(String s) {
    	 this(new StringReader(s));
     }
}
