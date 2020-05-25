import java.util.HashSet;
import java.util.Set;

public class ParsedGenre {
	private String name;

	public ParsedGenre(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ParsedGenre))
			return false;
		ParsedGenre g = (ParsedGenre) o;
		return g.getName().toLowerCase().equals(getName().toLowerCase()); // treat same-spelled names as the same genre
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Genre{name:").append(getName()).append("}");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return name.toLowerCase().hashCode();
	} // treat same-spelled names as the same genre

	public static void main(String[] args) {
		ParsedGenre g1 = new ParsedGenre("asdf");
		ParsedGenre g2 = new ParsedGenre("asdf");
		Set<ParsedGenre> myParsedGenres = new HashSet<>();
		myParsedGenres.add(g1);
		myParsedGenres.add(g2);
//		System.out.println(g1.equals(g2));
		System.out.println(myParsedGenres.size());
	}
}
