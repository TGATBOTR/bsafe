# bsafe

## Database

If no user exists, it will automatically create and login to a user called Fred Llewellyn.

## Code Examples
### Getting the current user
```
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import com.example.bsafe.Database.Models.User;
import com.android.bsafe.Auth.Session;

@AndroidEntityPoint
public class SomeClass {
	...

	@Inject
	public Session session;
	
	...

	public someMethod() {

		User currentUser = session.getUser();

		currentUser.uid;
		currentUser.firstName;
	}
}
```

###  Get Allergies for a user
```
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import com.example.bsafe.Database.Models.Allergy;
import com.example.bsafe.Database.Daos.AllergyDao;


public class SomeClass {
	...

	@Inject
	public AllergyDao allergyDao;

	...

	public void someMethod() {
		List <Allergy> allergies = allergyDao.getUserAllergies(12); // Get allergies for user with id 12
	}

```

## Translations

## Code example
```
// Define target language - there's a list of codes for them here: https://cloud.google.com/translate/docs/languages
String targetLang = "es";
// Define text to be translated
String translateThis = "Where is the nearest library?";
// Define a textView to output translation to
int textViewId = R.id.textView1;

// Create a listener --> put code in here that needs to be executed after the translation
OnTaskCompleted listener = new OnTaskCompleted() {
    @Override
    public void onTaskCompleted(String translation) {
	// Do something with result (setting a text view in this example)
	TextView textView = (TextView) findViewById(textViewId);
	textView.setText(translation);
	// Logging the result
	Log.d(TAG, "translated: " + translation);
    }
};

// Call the translation class
// Params: target language, string to be translated, listener
new TranslationAPI(targetLang, translateThis, listener).execute();
```

