package parser;

import java.util.ArrayList;
import java.util.Arrays;
public enum Color {
	MOD_RESET (Type.MODIFIER, ""),
	BOLD (Type.MODIFIER, "1"),
	UNDERSCORE (Type.MODIFIER, "2"),
	
	CL_RESET (Type.FOREGROUND, ""),
	BLACK (Type.FOREGROUND, "30"),
	RED (Type.FOREGROUND, "31"),
	GREEN (Type.FOREGROUND, "32"),
	YELLOW (Type.FOREGROUND, "33"),
	BLUE (Type.FOREGROUND, "34"),
	PURPLE (Type.FOREGROUND, "35"),
	CYAN (Type.FOREGROUND, "36"),
	WHITE (Type.FOREGROUND, "37"),
	
	BG_RESET (Type.BACKGROUND, ""),
	BG_BLACK (Type.BACKGROUND, "40"),
	BG_RED (Type.BACKGROUND, "41"),
	BG_GREEN (Type.BACKGROUND, "42"),
	BG_YELLOW (Type.BACKGROUND, "43"),
	BG_BLUE (Type.BACKGROUND, "44"),
	BG_PURPLE (Type.BACKGROUND, "45"),
	BG_CYAN (Type.BACKGROUND, "46"),
	BG_WHITE (Type.BACKGROUND, "47");
	
	public static String red (String r) { return apply(r, RED); }
	public static String green (String r) { return apply(r, GREEN); }
	public static String blue (String r) { return apply(r, BLUE); }
	public static String cyan (String r) { return apply(r, CYAN); }
	public static String yellow (String r) { return apply(r, YELLOW); }
	public static String purple (String r) { return apply(r, PURPLE); }
	public static String black (String r) { return apply(r, BLACK); }
	public static String white (String r) { return apply(r, WHITE); }
	
	public static String bgRed (String r) { return apply(r, BG_RED); }
	public static String bgGreen (String r) { return apply(r, BG_GREEN); }
	public static String bgBlue (String r) { return apply(r, BG_BLUE); }
	public static String bgCyan (String r) { return apply(r, BG_CYAN); }
	public static String bgYellow (String r) { return apply(r, BG_YELLOW); }
	public static String bgPurple (String r) { return apply(r, BG_PURPLE); }
	public static String bgBlack (String r) { return apply(r, BG_BLACK); }
	public static String bgWhite (String r) { return apply(r, BG_WHITE); }
	
	public final Type type;
	public final String value;
	
	private Color (Type type, String value) {
		this.type = type;
		this.value = value;
	}
	
	private static Section[] parse (String r) {
		ArrayList<Section> sections = new ArrayList<>();
		Section props = new Section(null);
		StringBuilder current = new StringBuilder();
		
		for (int i = 0; i < r.length(); i++) {
			if (i + 2 < r.length() && r.charAt(i) == '\u001B' && r.charAt(i + 1) == '[') {
				if (current.length() > 0) {
					sections.add(new Section(current.toString(), props.props));
					current.setLength(0);
				}
				
				StringBuilder b = new StringBuilder();
				
				for (i += 2; i < r.length(); i++) {
					if (r.charAt(i) == 'm') break;
					b.append(r.charAt(i));
				}
				
				String[] sec = b.toString().split(";");
				
				for (int ii = 0; ii < sec.length; ii++) {
					if (sec[ii].isEmpty() || sec[ii].equals("0")) {
						props = props.apply(MOD_RESET, CL_RESET, BG_RESET);
					}else for (int iii = 0; iii < Color.values().length; iii++) {
						if (Color.values()[iii].value.equals(sec[ii])) {
							props = props.apply(Color.values()[iii]);
						}
					}
				}
			}else {
				current.append(r.charAt(i));
			}
		}
		
		if (current.length() > 0) {
			sections.add(new Section(current.toString(), props.props));
		}
		
		return sections.toArray(new Section[0]);
	}
	
	private static String apply(String str, Color ... props) {
		Section[] sections = parse(str);
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < sections.length; i++) {
			b.append(sections[i].backApply(props));
		}
		
		return b.toString();
	}
	
	private static class Section {
		final String string;
		final Color[] props;
		
		public Section (String str, Color ... props) {
			this.string = str;
			this.props = simplify(props);
		}
		
		private static Color[] simplify (Color[] props) {
			Color[] n = new Color[props.length];
			int len = 0;
			
			for (int i = 0; i < props.length; i++) {
				Color prop = props[i];
				boolean added = false;
				
				for (int ii = 0; ii < len; ii++) {
					if (prop.type == n[ii].type) {
						n[ii] = prop;
						added = true;
						break;
					}
				}
				
				if (!added) n[len++] = prop;
			}
			
			for (int i = 0; i < len; i++) {
				if (n[i].value.isEmpty()) {
					for (int ii = i + 1; ii < len; ii++) {
						n[ii - 1] = n[ii];
					}
					
					len--;
					i--;
				}
			}
			
			return Arrays.copyOf(n, len);
		}
		
		public Section apply (Color ... props) {
			Color[] n = new Color[this.props.length + props.length];
			for (int i = 0; i < this.props.length; i++) n[i] = this.props[i];
			for (int i = 0; i < props.length; i++) n[i + this.props.length] = props[i];
			return new Section(this.string, n);
		}
		
		public Section backApply (Color ... props) {
			Color[] n = new Color[this.props.length + props.length];
			for (int i = 0; i < props.length; i++) n[i] = props[i];
			for (int i = 0; i < this.props.length; i++) n[i + props.length] = this.props[i];
			return new Section(this.string, n);
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder ();
			b.append("\u001B[");
			
			if (this.props.length == 0) {
				b.append("0");
			}else for (int i = 0; i < this.props.length; i++) {
				b.append(this.props[i].value);
				if (i < this.props.length - 1) b.append(';');
			}
			
			b.append("m");
			b.append(this.string);
			b.append("\u001B[0m");
			
			return b.toString();
		}
	}
	
	private enum Type {
		MODIFIER, FOREGROUND, BACKGROUND
	}
}
