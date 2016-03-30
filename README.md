As per problem statement the app has to calls a web service in particular time internal to fetch the data and show on activity.
So basically what we have to design is an app that has to execute a recurring task.
There are few approaches to schedule a server call, like: AlarmManager with BroadcastReciever, Timer (or ScheduledThreadPoolExecutor) and Service.
As the Alarm Manager is intended for cases where we want to have your application code run at a specific time, even if your application is not currently running (foreground).

Since the question applies to display the result while an activity is in foreground, so here in case we have best options to use is java.util.Timer class.
Timer classs fits well in our case as it have specific api (scheduleAtFixedRate) to schedule an action to occur at regular intervals on a background thread.

I have used two buttons one for start request other for end request. One input text to get user input value in seconds and a textview to show the results.
While recurring task is running we can either disable input textview and request button or allow user to give input which will reschedule the task by cancelling the previous request and scheduling the new one, here i followed the second approach.
In case of no network connection and onstop of activity , timer and network requests are cancelled which releases the timer's thread and other resources.
Shows indeterminate progress bar just for the time in which request is fetched.

For network handling i prefered using Retrofit library as networking frameworks which is a powerful framework for interacting with APIs and sending network requests with OkHttp.
I have added mechanism to cancel a particular request or all requests. ClientGenerator class is used to set up RestAdapter for handling network requests and also set Okhttp client to RestAdapter.
Override the onError callback to handle the cases of no nework connection and timeout scenarios{set timeout to 1 sec}.

In case of no network and time out conditions if data is not sufficient as needed, showing whatever we have at that time.
And also maintaining the lastest five fetched data in sharedPreferences for using it in next start of app in no network or timeout case.
