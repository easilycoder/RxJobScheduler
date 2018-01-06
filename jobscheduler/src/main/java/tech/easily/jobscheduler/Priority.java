package tech.easily.jobscheduler;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static tech.easily.jobscheduler.Priority.PRIORITY_MAX;
import static tech.easily.jobscheduler.Priority.PRIORITY_MIN;
import static tech.easily.jobscheduler.Priority.PRIORITY_NORMAL;

/**
 * Created by lemon on 06/01/2018.
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({PRIORITY_MAX, PRIORITY_NORMAL, PRIORITY_MIN})
public @interface Priority {
    int PRIORITY_MAX = 10;
    int PRIORITY_NORMAL = 5;
    int PRIORITY_MIN = 1;
}
