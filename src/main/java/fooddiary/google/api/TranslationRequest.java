package fooddiary.google.api;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TranslationRequest {
    private final static String GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY");


    public String translate(String textForTranslation) {
        Translate translate = TranslateOptions.newBuilder().setApiKey(GOOGLE_API_KEY).build().getService();
        Translation translation = translate.translate(
                textForTranslation,
                TranslateOption.sourceLanguage("ru"),
                TranslateOption.targetLanguage("en")
        );
        return translation.getTranslatedText();
    }
}
