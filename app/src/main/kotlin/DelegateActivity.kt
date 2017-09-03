import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.pelsoczi.adam.tapthat.AlarmsActivity
import com.pelsoczi.adam.tapthat.MyApplication
import com.pelsoczi.adam.tapthat.RingingActivity
import com.pelsoczi.adam.tapthat.app.MediaService

class DelegateActivity : LifecycleActivity() {

    val NAME = DelegateActivity::getLocalClassName

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val isRinging = Application.isRinging
        val isSnoozed = Application.isSnoozing

        // determine which activity should be started
        if (isRinging || isSnoozed) {
            if (isSnoozed) {
                // play media
                val media = Intent(applicationContext, MediaService::class.java)
                media.action = MediaService.ACTION_PLAY
                applicationContext.startService(media)

                // todo the app was launched while snoozed, modify the global state
                MyApplication.getInstance().setSnoozing(false)
                MyApplication.getInstance().doScheduling(false)
            }
            // present the user with ringing alarm
            startActivity( Intent(applicationContext, RingingActivity::class.java) )
        } else {
            // present the user the primary activity
            startActivity( Intent(applicationContext, AlarmsActivity::class.java) )
        }
        // done
        finish()
    }
}