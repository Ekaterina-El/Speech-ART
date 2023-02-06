package el.ka.speechart.viewModel

import android.app.Application
import el.ka.speechart.other.UserRole

class AdminViewModel(application: Application) : ViewModelWithSearchUsers(application, UserRole.SPECIALIST) {
}