package tcpChatClient;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class StyledDocument
{
	private final Style headerStyle;
	private static final int HEADERINDEX = 0;
	private final Style standardStyle;
	private static final int STANDARDINDEX = 0;
	private final Style userStyle;
	private static final int USERINDEX = 1;
	private final Style chatStyle;
	private static final int CHATINDEX = 2;
	private final Style whisperStyle;
	private static final int WHISPERINDEX = 3;
	private final Style errorStyle;
	private static final int ERRORINDEX = 1;
	private final Style exclamationStyle;
	private static final int EXCLAMATIONINDEX = 0;
	private final Style tempStyle;
	private final ArrayList<Color> lightColor = new ArrayList<Color>();
	private final ArrayList<Color> darkColor = new ArrayList<Color>();
	
	// 0 - BG Light, 1 - BG Dark, 2 - FG Light, 3 - FG Dark
	private ArrayList<Color> buttonColors = new ArrayList<Color>();
	private ArrayList<Color> inputFieldColors = new ArrayList<Color>();
	private ArrayList<Color> userPaneColors = new ArrayList<Color>();
	private ArrayList<Color> textPaneColors = new ArrayList<Color>();
	private ArrayList<Color> scrollColors = new ArrayList<Color>();
	
	private StyleContext sc;
	private boolean darkTheme;
	
	public StyledDocument()
	{
		sc = new StyleContext();
		buttonColors.add(new JButton().getBackground());
		buttonColors.add(Color.black);
		buttonColors.add(Color.black);
		buttonColors.add(Color.white);
		scrollColors.add(new JScrollPane().getVerticalScrollBar().getBackground());
		scrollColors.add(Color.black);
		inputFieldColors.add(Color.lightGray);
		inputFieldColors.add(Color.darkGray);
		inputFieldColors.add(Color.black);
		inputFieldColors.add(Color.white);
		userPaneColors.add(Color.yellow);
		userPaneColors.add(new Color(0xa4, 0x5c, 0x12));
		textPaneColors.add(Color.white);
		textPaneColors.add(new Color(0x20, 0x20, 0x20));
		
		darkTheme = false;
		
		lightColor.add(Color.black);			// 0
		darkColor.add(Color.white);				// 0
		lightColor.add(Color.red); 				// 1
		darkColor.add(Color.red);				// 1
		lightColor.add(Color.gray);				// 2
		darkColor.add(Color.lightGray);			// 2
		lightColor.add(new Color(34, 139, 34));	// 3
		darkColor.add(Color.green);				// 3
		
		headerStyle = sc.addStyle("HEADER", null);
	    headerStyle.addAttribute(StyleConstants.Foreground, lightColor.get(HEADERINDEX));
	    headerStyle.addAttribute(StyleConstants.Bold, true);
	    standardStyle = sc.addStyle("STANDARD", null);
	    standardStyle.addAttribute(StyleConstants.Foreground, lightColor.get(STANDARDINDEX));
	    userStyle = sc.addStyle("USER", null);
	    userStyle.addAttribute(StyleConstants.Foreground, lightColor.get(USERINDEX));
	    chatStyle = sc.addStyle("CHAT", null);
	    chatStyle.addAttribute(StyleConstants.Foreground, lightColor.get(CHATINDEX));
	    chatStyle.addAttribute(StyleConstants.Italic, true);
	    whisperStyle = sc.addStyle("WHISPER", null);
	    whisperStyle.addAttribute(StyleConstants.Foreground, lightColor.get(WHISPERINDEX));
	    whisperStyle.addAttribute(StyleConstants.Italic, true);
	    errorStyle = sc.addStyle("ERROR", null);
	    errorStyle.addAttribute(StyleConstants.Foreground, lightColor.get(ERRORINDEX));
	    exclamationStyle = sc.addStyle("EXCLAMATION", null);
	    exclamationStyle.addAttribute(StyleConstants.Foreground, lightColor.get(EXCLAMATIONINDEX));
	    exclamationStyle.addAttribute(StyleConstants.Italic, true);
	    exclamationStyle.addAttribute(StyleConstants.Bold, true);
	    
	    tempStyle = sc.addStyle("TEMP", null);
	}
	
	public StyleContext getStyleContext()
	{
		return sc;
	}
	public Style getStyle (String type)
	{
		switch (type)
		{
		case "header":
			return headerStyle;
		case "standard":
			return standardStyle;
		case "user":
			return userStyle;
		case "whisper":
			return whisperStyle;
		case "error":
			return errorStyle;
		case "exclamation":
			return exclamationStyle;
		case "chat":
			return chatStyle;
		default:
			return null;
		}
	}
	public Color getElementColor (String element, String ground)
	{
		int index = 0;
		if (ground.equals("FG"))
			index = 2;
		if (darkTheme)
			++index;
		switch (element)
		{
		case "button":
			return buttonColors.get(index);
		case "scrollbar":
			return scrollColors.get(index);
		case "userPane":
			return userPaneColors.get(index);
		case "textPane":
			return textPaneColors.get(index);
		case "inputField":
			return inputFieldColors.get(index);
		default:
			return Color.white;
		}
	}
	public void SwapTheme()
	{
		darkTheme = !darkTheme;
		headerStyle.removeAttribute(StyleConstants.Foreground);
		headerStyle.addAttribute(		StyleConstants.Foreground, darkTheme ? darkColor.get(HEADERINDEX)		: lightColor.get(HEADERINDEX));
		standardStyle.removeAttribute(StyleConstants.Foreground);
		standardStyle.addAttribute(		StyleConstants.Foreground, darkTheme ? darkColor.get(STANDARDINDEX) 	: lightColor.get(STANDARDINDEX));
		userStyle.removeAttribute(StyleConstants.Foreground);
		userStyle.addAttribute(			StyleConstants.Foreground, darkTheme ? darkColor.get(USERINDEX) 		: lightColor.get(USERINDEX));
		chatStyle.removeAttribute(StyleConstants.Foreground);
		chatStyle.addAttribute(			StyleConstants.Foreground, darkTheme ? darkColor.get(CHATINDEX) 		: lightColor.get(CHATINDEX));
		whisperStyle.removeAttribute(StyleConstants.Foreground);
		whisperStyle.addAttribute(		StyleConstants.Foreground, darkTheme ? darkColor.get(WHISPERINDEX) 		: lightColor.get(WHISPERINDEX));
		errorStyle.removeAttribute(StyleConstants.Foreground);
		errorStyle.addAttribute(		StyleConstants.Foreground, darkTheme ? darkColor.get(ERRORINDEX) 		: lightColor.get(ERRORINDEX));
		exclamationStyle.removeAttribute(StyleConstants.Foreground);
		exclamationStyle.addAttribute(	StyleConstants.Foreground, darkTheme ? darkColor.get(EXCLAMATIONINDEX) 	: lightColor.get(EXCLAMATIONINDEX));
	}
	public Style complement(AttributeSet attributes)
	{
		Color color;
		if (darkTheme)
			color = darkColor.get(lightColor.indexOf(attributes.getAttribute(StyleConstants.Foreground)));
		else
			color = lightColor.get(darkColor.indexOf(attributes.getAttribute(StyleConstants.Foreground)));
		switch (attributes.getAttributeCount())
		{
		case 1:
			if (standardStyle.getAttribute(StyleConstants.Foreground) == color)
				return standardStyle;
			if (userStyle.getAttribute(StyleConstants.Foreground) == color)
				return userStyle;
			if (errorStyle.getAttribute(StyleConstants.Foreground) == color)
				return errorStyle;
			return standardStyle;
		case 2:
			if (headerStyle.getAttribute(StyleConstants.Foreground) == color && attributes.getAttribute(StyleConstants.Bold) == headerStyle.getAttribute(StyleConstants.Bold))
				return headerStyle;
			if (chatStyle.getAttribute(StyleConstants.Foreground) == color && attributes.getAttribute(StyleConstants.Italic) == chatStyle.getAttribute(StyleConstants.Italic))
				return chatStyle;
			if (whisperStyle.getAttribute(StyleConstants.Foreground) == color && attributes.getAttribute(StyleConstants.Italic) == whisperStyle.getAttribute(StyleConstants.Italic))
				return chatStyle;
			return standardStyle;
		case 3:
			if (exclamationStyle.getAttribute(StyleConstants.Foreground) == color && attributes.getAttribute(StyleConstants.Bold) == exclamationStyle.getAttribute(StyleConstants.Bold) && attributes.getAttribute(StyleConstants.Italic) == exclamationStyle.getAttribute(StyleConstants.Italic))
				return chatStyle;
			return standardStyle;
		default:
			return standardStyle;
		}
	}

}
