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
