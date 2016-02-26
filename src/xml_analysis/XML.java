package xml_analysis;

import java.io.InputStream;



/**
 * Проводит анализ лексем в документе xml.
 * 
 * @author Данилов Владислав ПИ-41
 */
@SuppressWarnings("boxing")
public class XML {
    public static String errorStack = "";
    public static final String LINE = "______________________________________________________________\n"; 
    /** Символ '&amp;'. */
    public static final Character AMP = '&';

    /** Символ  '''. */
    public static final Character APOS = '\'';

    /** Символ  '!'. */
    public static final Character BANG = '!';

    /** Символ  '='. */
    public static final Character EQ = '=';

    /** Символ  '>'. */
    public static final Character GT = '>';

    /** Символ  '&lt;'. */
    public static final Character LT = '<';

    /** Символ  '?'. */
    public static final Character QUEST = '?';

    /** Символ  '"'. */
    public static final Character QUOT = '"';

    /** Символ  '/'. */
    public static final Character SLASH = '/';

    /**
     * Проверяет на пробелы в названиях тегов и атрибутов
     * и записывает ошибку при их наличии
     * 
     * @param string строка
     */
    public static void noSpace(String string){
        int i, length = string.length();
        if (length == 0) {
        	System.out.print(" - Error: " + "Empty string." + "\n");
        }
        for (i = 0; i < length; i += 1) {
            if (Character.isWhitespace(string.charAt(i))) {
            	System.out.print(" - Error: " + "'" + string
                        + "' contains a space character." + "\n");
            	errorStack = errorStack + (" - Error: " + "'" + string
                        + "' contains a space character." + "\n");
            }
        }
    }

    /**
     * Проводит анализ xml документа
     * 
     * @param x
     *            XMLTokener содержащий исходную строку.
     * @param context
     *            Строка формируемая на основе анализа xml документа.
     * @param name
     *            Имя тега.
     * @return true если есть закрывающий тег.
     */
    private static boolean parse(XMLTokener x, StringBuilder context, String name)
            {
        char c;
        int i;
        StringBuilder tempContext = new StringBuilder();
        String string;
        String tagName;
        Object token;
        token = x.nextToken();
        if (token == BANG) {
        	context.append(token.toString() + " - восклицательный знак \n");
            c = x.next();
            if (c == '-') {
            	context.append(c + " - тире \n");
            	context.append(LINE);
                if (x.next() == '-') {
                	context.append(c + " - тире \n");
                	context.append(LINE);
                	context.append("коментарий пропущен\n");
                	context.append(LINE);
                	context.append(c + " - тире \n");
                	context.append(LINE);
                	context.append(c + " - тире \n");
                	context.append(LINE);
                	context.append("> - закрывающий тег\n");
                	context.append(LINE);
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[') {
                token = x.nextToken();
                if ("CDATA".equals(token)) {
                    if (x.next() == '[') {
                        string = x.nextCDATA();
                        if (string.length() > 0) {
                            context.append(string);
                        }
                        return false;
                    }
                }
                
                System.out.print(" - Error: " + "Expected 'CDATA['" + "\n");
                errorStack = errorStack + (" - Error: " + "Expected 'CDATA['" + "\n");
                return false;
            }
            i = 1;
            do {
                token = x.nextMeta();
                if (token == null) {
                	System.out.print(" - Error: " + "Missing '>' after '<!'." + "\n");
                	errorStack = errorStack + (" - Error: " + "Missing '>' after '<!'." + "\n");            	
                	return false;

                } else if (token == LT) {
                    i += 1;
                } else if (token == GT) {
                    i -= 1;
                }
            } while (i > 0);
            return false;
        } else if (token == QUEST) {
            x.skipPast("?>");
            return false;
        } else if (token == SLASH) {
        	context.append(token.toString() + " - слэш \n");
        	context.append(LINE);
            token = x.nextToken();
            if (name == null) {
            	System.out.print(" - Error: " + "Mismatched close tag " + token + "\n");
            	errorStack = errorStack + (" - Error: " + "Mismatched close tag " + token + "\n"); 
            	return false;
            } else
            if (!token.equals(name)) {
            	System.out.print(" - Error: " + "Mismatched " + name + " and " + token + "\n");
            	errorStack = errorStack + (" - Error: " + "Mismatched " + name + " and " + token + "\n"); 
            	return false;
            } else
            if (x.nextToken() != GT) {
            	System.out.print(" - Error: " + "Misshaped close tag" + "\n");
            	errorStack = errorStack + (" - Error: " + "Misshaped close tag" + "\n"); 
            	return false;
            } else
            {
            	context.append(token + " - закрывающий тег \n");
            	context.append(LINE);
            }
            return true;

        } else if (token instanceof Character) {
        	System.out.print(" - Error: " + "Misshaped tag" + "\n");
        	errorStack = errorStack + (" - Error: " + "Misshaped tag" + "\n");
        	return false;
        } else {
        	
            tagName = (String) token;
            
            token = null;
            for (;;) {
                if (token == null) {
                    token = x.nextToken();
                }
                if (token instanceof String) {
                    string = (String) token;
                    
                    token = x.nextToken();
                    if (token == EQ) {    
                    	tempContext.append(string + " - атрибут \n");
                    	tempContext.append(LINE);
                        tempContext.append( token.toString()+ " - оператор присвоения \n");
                        tempContext.append(LINE);
                        token = x.nextToken();
                        if (!(token instanceof String)) {
                        	System.out.print(" - Error: " + "Missing value" + "\n");
                        	errorStack = errorStack + (" - Error: " + "Missing value" + "\n");
                        	return false;
                        }
            
                        tempContext.append((String) token + " - значение атрибута \n");
                        tempContext.append(LINE);
                        token = null;
                    } else {
                        tempContext.append(string);
                        tempContext.append(LINE);
                    }


                } else if (token == SLASH) {
                    if (x.nextToken() != GT) {
                    	System.out.print(" - Error: " + "Misshaped tag" + "\n");
                    	errorStack = errorStack + (" - Error: " + "Misshaped tag" + "\n");
                    	return false;
                    }
                    if (tempContext.length() > 0) {
                        context.append(tagName);
                        context.append(tempContext);
                    } else {
                        context.append(tagName);
                    }
                    return false;

                } else if (token == GT) {
                     tempContext.append(token.toString() + " - закрывающая скобка \n");
                     tempContext.append(LINE);
                    for (;;) {
                        token = x.nextContent();
                        if (token == null) {
                            if (tagName != null) {
                            	System.out.print(" - Error: " + "Unclosed tag " + tagName + "\n");
                            	errorStack = errorStack + (" - Error: " + "Unclosed tag " + tagName + "\n");
                            	 return false;
                            }
                            return false;
                        } else if (token instanceof String) {
                            string = (String) token;
                            if (string.length() > 0) {
                                tempContext.append(string + " - содержимое тега \n");
                                tempContext.append(LINE);
                            }

                        } else if (token == LT) {
                        	tempContext.append(token.toString() + " - открывающая скобка \n");
                        	tempContext.append(LINE);
                            if (parse(x, tempContext, tagName)) {
                                if (tempContext.length() == 0) {
                                    context.append(tagName + " - название тега \n");
                                    context.append(LINE);
                                } else if (tempContext.length() == 1) {
                                    context.append(tagName + " - название тега \n");
                                    context.append(LINE);
                                } else {
                                    context.append(tagName + " - название тега \n");
                                    context.append(LINE);
                                    context.append(tempContext);
                                }
                                return false;
                            }
                        }
                    }
                } else {
                	System.out.print(" - Error: " + "Misshaped tag" + tagName + "\n");
                	errorStack = errorStack + (" - Error: " + "Misshaped tag" + tagName + "\n");
                	return false;
                }
            }
        }
    }
    

    /**
     * Лексический анализ xml документа,
     * проверка на валидацию и вывод в консоль результатов
     * @param string
     * Строка xml содержимого.
     * @return возвращает объект StringBuilder содержащий результат анализа
     */
    public static StringBuilder xmlAnalysisMethod(InputStream string){
    	StringBuilder sb = new StringBuilder();
        XMLTokener x = new XMLTokener(string);
        while (x.more() && x.skipPast("<")) {
            parse(x, sb, null);
        }
        if(errorStack != ""){
        	sb.setLength(0);
        	sb.append(errorStack);
        }else{
        	sb.insert(0, "< - открывающая скобка \n" + LINE);
        	sb.append("> - закрывающая скобка \n");
        }
        return sb;
    }
}
