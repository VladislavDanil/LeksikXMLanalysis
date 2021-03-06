package xml_analysis;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;



/**
 * �������� ������ ������ � ��������� xml.
 * 
 * @author ������� ��������� ��-41
 */
@SuppressWarnings("boxing")
public class XML {
    public static String errorStack = "";
    
    public static final String LINE = "______________________________________________________________\n"; 
    /** ������ '&amp;'. */
    public static final Character AMP = '&';

    /** ������  '''. */
    public static final Character APOS = '\'';

    /** ������  '!'. */
    public static final Character BANG = '!';

    /** ������  '='. */
    public static final Character EQ = '=';

    /** ������  '>'. */
    public static final Character GT = '>';

    /** ������  '&lt;'. */
    public static final Character LT = '<';

    /** ������  '?'. */
    public static final Character QUEST = '?';

    /** ������  '"'. */
    public static final Character QUOT = '"';

    /** ������  '/'. */
    public static final Character SLASH = '/';

    /**
     * ��������� �� ������� � ��������� ����� � ���������
     * � ���������� ������ ��� �� �������
     * 
     * @param string ������
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
     * �������� ������ xml ���������
     * 
     * @param x
     *            XMLTokener ���������� �������� ������.
     * @param context
     *            ������ ����������� �� ������ ������� xml ���������.
     * @param name
     *            ��� ����.
     * @return true ���� ���� ����������� ���.
     */
    private static boolean parse(XMLTokener x, StringBuilder context, String name)
            {
    	int characterPosition;
    	int position = 0;
    	int linePosition = 0;
        char c;
        int i = 0;
        StringBuilder tempContext = new StringBuilder();
        String string = null;
        String tagName = "";
        String tagNameTemp = "";
        Object token;
        position = (int) x.index +1;
        linePosition = (int) x.line;
        characterPosition = (int) x.character;
        /*����� ����� ��������� �������� �����*/
        token = x.nextToken();
        /*������������� ������, �� ��� ��*/
        tagNameTemp = token.toString() + "(" + (x.indexTag +1) +"," + x.lineTag+"," + x.characterTag+")";
        position = (int) x.index + 1;
        linePosition = (int) x.line;
        characterPosition = (int) x.character;
        if (token == BANG) {
        	//context.append(token.toString() + " ("+ x.index + ")" + " - ��������������� ���� \n");
            c = x.next();
            if (c == '-') {
            	/*
                context.append(c + " - ���� \n");
            	context.append(LINE);*/
                if (x.next() == '-') {
                	//����� �������� ����� ��������� �����������
                	/*context.append(c + " - ���� \n");
                	context.append(LINE);
                	context.append("���������� ��������\n");
                	context.append(LINE);
                	context.append(c + " - ���� \n");
                	context.append(LINE);
                	context.append(c + " - ���� \n");
                	context.append(LINE);
                	context.append("> - ����������� ���\n");
                	context.append(LINE);*/
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
        	context.append(token.toString()+" ("+ ((int)x.index + 1) + "," + x.line + "," + x.character + ")" + "- ���� \n");
        	context.append(LINE);
            token = x.nextToken();
            position = (int) x.indexTag +1;
        	linePosition = (int) x.lineTag;
        	characterPosition = (int) x.characterTag;
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
            	context.append(token +" ("+ position + "," + linePosition + "," + characterPosition + ")"+ " - ����������� ��� \n");
            	context.append(LINE);
            	context.append(x.previous+" ("+ (x.index +1) + "," + x.line + "," + x.character + ")" + " - ����������� ������ \n");
            	context.append(LINE);
            }
            return true;

        } else if (token instanceof Character) {
        	System.out.print(" - Error: " + "Misshaped tag" + "\n");
        	errorStack = errorStack + (" - Error: " + "Misshaped tag" + "\n");
        	return false;
        } else {
           
            try{
            	 tagName = (String) token;
                }catch(ClassCastException e){  	      	
                }
            
            token = null;
            for (;;) {
                if (token == null) {
                	position = (int) x.index + 1;
                	linePosition = (int) x.line;
                	characterPosition = (int) x.character;
                    token = x.nextToken();
                }
                if (token instanceof String) {
                    string = (String) token + " ("+ position + "," + linePosition + "," + characterPosition + ")";
                    position = (int) x.index +1;
                    linePosition = (int) x.line;
                    characterPosition = (int) x.character;
                    token = x.nextToken();
                    if (token == EQ) {    
                    	tempContext.append(string +" - ������� \n");
                    	tempContext.append(LINE);
                        tempContext.append( token.toString() + " ("+ position+ "," + linePosition + "," +  characterPosition + ")"+  " - �������� ���������� \n");
                        tempContext.append(LINE);
                        position = (int) x.index + 1;
                        linePosition = (int) x.line;
                        characterPosition = (int) x.character;
                        token = x.nextToken();
                        if (!(token instanceof String)) {
                        	System.out.print(" - Error: " + "Missing value" + "\n");
                        	errorStack = errorStack + (" - Error: " + "Missing value" + "\n");
                        	return false;
                        }
            
                        tempContext.append((String) token+ " ("+ position + "," + linePosition + "," + characterPosition +")" + " - �������� �������� \n");
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
                	 
                     tempContext.append(token.toString()+" ("+ position + "," + linePosition + "," + characterPosition + ")" + " - ����������� ������ \n");
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
                                tempContext.append(string + " - ���������� ���� \n");
                                tempContext.append(LINE);
                            }

                        } else if (token == LT) {
                        	tempContext.append(token.toString() + " ("+ ((int)x.index+1) + "," + x.line + "," + x.character + ")" + " - ����������� ������ \n");
                        	tempContext.append(LINE);
                            if (parse(x, tempContext, tagName)) {
                                if (tempContext.length() == 0) {
                                    context.append(tagNameTemp + " - �������� ���� \n");
                                    context.append(LINE);
                                } else if (tempContext.length() == 1) {
                                    context.append(tagNameTemp + " - �������� ���� \n");
                                    context.append(LINE);
                                } else {
                                    context.append(tagNameTemp + " - �������� ���� \n");
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
     * ����������� ������ xml ���������,
     * �������� �� ��������� � ����� � ������� �����������
     * @param string
     * ������ xml �����������.
     * @return ���������� ������ StringBuilder ���������� ��������� �������
     */
    public static StringBuilder xmlAnalysisMethod(InputStream string){
    	StringBuilder sb = new StringBuilder();
        XMLTokener x = null;
		try {
			x = new XMLTokener(string);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int position = 0;
        int line = 0;
        int character = 0;
        while (x.more() && x.skipPast("<")) {
        	position = (int) x.index + 1;
        	line = (int) x.line;
        	character = (int) x.character;
            parse(x, sb, null);
        }
        if(errorStack != ""){
        	sb.setLength(0);
        	sb.append(errorStack);
        }else{
        	sb.insert(0, "< ("+position+","+line+","+character+")- ����������� ������ \n" + LINE);
        }
        return sb;
    }
}
