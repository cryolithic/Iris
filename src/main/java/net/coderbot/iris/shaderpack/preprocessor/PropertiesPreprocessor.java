package net.coderbot.iris.shaderpack.preprocessor;

import net.coderbot.iris.Iris;
import net.coderbot.iris.gl.shader.StandardMacros;
import org.anarres.cpp.Feature;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;

import java.util.List;
import java.util.Map;

public class PropertiesPreprocessor {
	public static String process(List<String> booleanValues, Map<String, String> stringValues, String source) {
		source = "#define MC_VERSION " + StandardMacros.getMcVersion() + "\n" + source + "\n";

		return preprocessSource(booleanValues, stringValues, source);
	}

	// Derived from ShaderProcessor.glslPreprocessSource, which is derived from GlShader from Canvas, licenced under LGPL
	public static String preprocessSource(List<String> booleanValues, Map<String, String> stringValues, String source) {
		@SuppressWarnings("resource")
		final Preprocessor pp = new Preprocessor();
		pp.setListener(new PropertiesCommentListener());
		try {
			for (String value : booleanValues) {
				pp.addMacro(value);
			}
		} catch (LexerException e) {
				e.printStackTrace();
		}
		stringValues.forEach((name, value) -> {
			try {
				pp.addMacro(name, value);
			} catch (LexerException e) {
				e.printStackTrace();
			}
		});
		pp.addInput(new StringLexerSource(source, true));
		pp.addFeature(Feature.KEEPCOMMENTS);

		final StringBuilder builder = new StringBuilder();

		try {
			for (;;) {
				final Token tok = pp.token();
				if (tok == null) break;
				if (tok.getType() == Token.EOF) break;
				builder.append(tok.getText());
			}
		} catch (final Exception e) {
			Iris.logger.error("Properties pre-processing failed", e);
		}

		source = builder.toString();

		return source;
	}
}
