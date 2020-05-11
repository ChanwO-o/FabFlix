import java.util.HashSet;
import java.util.Set;

public class Genre {
	private String name;

	public Genre(String name) {
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
		if (!(o instanceof Genre))
			return false;
		Genre g = (Genre) o;
		return g.getName().equals(getName());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Genre Details - ");
		sb.append("name:").append(getName());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public static void main(String[] args) {
		Genre g1 = new Genre("asdf");
		Genre g2 = new Genre("asdf");
		Set<Genre> myGenres = new HashSet<>();
		myGenres.add(g1);
		myGenres.add(g2);
//		System.out.println(g1.equals(g2));
		System.out.println(myGenres.size());
	}
}
