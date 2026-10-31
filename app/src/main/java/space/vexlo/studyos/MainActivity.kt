package space.vexlo.studyos

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

private val Navy = Color(0xFF101827)
private val Panel = Color(0xFF1B2940)
private val Blue = Color(0xFF5B8CFF)
private val Muted = Color(0xFF9EACC2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createStudyChannel(this)
        setContent { StudyOSApp(this) }
    }
}

data class StudyBlock(val time: String, val title: String, val course: String, val duration: String)

private val blocks = listOf(
    StudyBlock("5:00 AM", "Wake up", "Start your day", "Alarm"),
    StudyBlock("5:30 AM", "Deep study", "Computer Architecture", "2 hours"),
    StudyBlock("8:00 AM", "Deep study", "Database Design", "2 hours"),
    StudyBlock("10:30 AM", "Practice", "Visual Programming (C#)", "2 hours"),
    StudyBlock("2:00 PM", "Revision", "User Interface Design", "2 hours"),
    StudyBlock("7:00 PM", "Quick review", "Optional recap", "30 min")
)

@Composable
fun StudyOSApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("studyos", Context.MODE_PRIVATE) }
    var completed by remember { mutableIntStateOf(prefs.getInt("completed", 0)) }
    var alarmOn by remember { mutableStateOf(prefs.getBoolean("alarm_on", false)) }
    var selected by remember { mutableStateOf("Home") }
    var notes by remember { mutableStateOf(prefs.getString("notes", "") ?: "") }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Blue, background = Navy, surface = Panel,
        onPrimary = Color.White, onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Navy,
            bottomBar = {
                NavigationBar(containerColor = Panel) {
                    listOf("Home", "Timetable", "Progress", "Settings").forEach { item ->
                        NavigationBarItem(
                            selected = selected == item,
                            onClick = { selected = item },
                            icon = {
                                Icon(
                                    when(item) {
                                        "Timetable" -> Icons.Default.CalendarMonth
                                        "Progress" -> Icons.Default.Insights
                                        "Settings" -> Icons.Default.Settings
                                        else -> Icons.Default.Home
                                    }, contentDescription = item
                                )
                            },
                            label = { Text(item) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue, selectedTextColor = Blue,
                                indicatorColor = Navy, unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp)
            ) {
                item {
                    Text("VEXLO", color = Blue, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("StudyOS", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                    Text("Your focus. Your progress. Your future.", color = Muted)
                }
                when (selected) {
                    "Home" -> {
                        item {
                            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(18.dp)) {
                                    Text("EXAM COUNTDOWN", color = Muted)
                                    Text("27 NOV 2026", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text("Build consistency, one session at a time.", color = Muted)
                                }
                            }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard("8 hrs", "Daily target", Modifier.weight(1f))
                                StatCard("$completed", "Sessions done", Modifier.weight(1f))
                            }
                        }
                        item {
                            Text("Today's study plan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        items(blocks) { block ->
                            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
                                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(block.time, color = Blue, fontWeight = FontWeight.Bold)
                                        Text(block.title, fontWeight = FontWeight.SemiBold)
                                        Text(block.course, color = Muted)
                                    }
                                    Text(block.duration, color = Muted)
                                }
                            }
                        }
                        item {
                            Button(
                                onClick = {
                                    completed++
                                    prefs.edit().putInt("completed", completed).apply()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Blue)
                            ) { Icon(Icons.Default.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text("Complete a study session") }
                        }
                    }
                    "Timetable" -> {
                        item { Text("Your timetable", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                        items(blocks) { b ->
                            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                                ListItem(
                                    headlineContent = { Text(b.title) },
                                    supportingContent = { Text("${b.time} • ${b.course}") },
                                    trailingContent = { Text(b.duration, color = Blue) },
                                    colors = ListItemDefaults.colors(containerColor = Panel, headlineColor = Color.White, supportingColor = Muted)
                                )
                            }
                        }
                    }
                    "Progress" -> {
                        item { Text("Your progress", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                        item { StatCard("$completed", "Completed sessions", Modifier.fillMaxWidth()) }
                        item { Text("Course checklist", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                        items(listOf("Computer Architecture", "Database Design", "User Interface Design", "Research Methods", "Visual Programming (C#)", "Gender and ICT")) { course ->
                            var checked by remember(course) { mutableStateOf(prefs.getBoolean(course, false)) }
                            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked, onCheckedChange = {
                                        checked = it; prefs.edit().putBoolean(course, it).apply()
                                    })
                                    Text(course)
                                }
                            }
                        }
                    }
                    else -> {
                        item { Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                        item {
                            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Daily study reminders", fontWeight = FontWeight.Bold)
                                    Text("Schedule the 5:00 AM wake-up and study reminders.", color = Muted)
                                    Spacer(Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Switch(checked = alarmOn, onCheckedChange = { enabled ->
                                            alarmOn = enabled
                                            prefs.edit().putBoolean("alarm_on", enabled).apply()
                                            if (enabled) {
                                                if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                }
                                                scheduleDailyAlarm(context, 5, 0, "Wake up — Vexlo StudyOS")
                                            } else cancelAlarm(context)
                                        })
                                        Spacer(Modifier.width(8.dp))
                                        Text(if (alarmOn) "Reminders enabled" else "Reminders disabled")
                                    }
                                    Text("Android may require you to allow notifications and exact alarms in Settings.", color = Muted, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = notes, onValueChange = { notes = it; prefs.edit().putString("notes", it).apply() },
                                label = { Text("Study notes / goals") }, modifier = Modifier.fillMaxWidth(),
                                minLines = 4
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(value, color = Blue, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, color = Muted)
        }
    }
}

fun createStudyChannel(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(NotificationChannel("study_reminders", "Study reminders", NotificationManager.IMPORTANCE_HIGH))
}

fun scheduleDailyAlarm(context: Context, hour: Int, minute: Int, message: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, StudyAlarmReceiver::class.java).putExtra("message", message)
    val pending = PendingIntent.getBroadcast(context, 500, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
    }
    if (Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
    } else {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
    }
}

fun cancelAlarm(context: Context) {
    val intent = Intent(context, StudyAlarmReceiver::class.java)
    val pending = PendingIntent.getBroadcast(context, 500, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pending)
}

class StudyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "Time to study!"
        val notification = NotificationCompat.Builder(context, "study_reminders")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Vexlo StudyOS")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(500, notification)
        }
        // Schedule the next daily occurrence.
        scheduleDailyAlarm(context, 5, 0, "Wake up — Vexlo StudyOS")
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (context.getSharedPreferences("studyos", Context.MODE_PRIVATE).getBoolean("alarm_on", false)) {
            scheduleDailyAlarm(context, 5, 0, "Wake up — Vexlo StudyOS")
        }
    }
}
