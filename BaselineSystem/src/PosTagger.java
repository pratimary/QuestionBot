import java.io.IOException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {
	private static String Text;
	private static String TaggedText;
	
	public PosTagger(String text) {
		Text = text;
	}
	public String PosTag() {
		MaxentTagger tagger;
			tagger = new MaxentTagger("tagger/english-left3words-distsim.tagger");
			TaggedText = tagger.tagString(Text);
		
		
		return TaggedText;
	}
}
