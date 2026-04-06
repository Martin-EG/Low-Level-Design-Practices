interface NotificationStrategy {
  sendNotification(message: string): void;
}

class EmailNotification implements NotificationStrategy {
  sendNotification(message: string): void {
    console.log(`Sending email notification: ${message}`);
  }
}

class SMSNotification implements NotificationStrategy {
  sendNotification(message: string): void {
    console.log(`Sending SMS notification: ${message}`);
  }
}

class PushNotification implements NotificationStrategy {
  sendNotification(message: string): void {
    console.log(`Sending push notification: ${message}`);
  }
}

class Notifier {
  constructor(private notificationStrategy: NotificationStrategy) {}

  notify(message: string): void {
    if(!message) throw new Error("Message cannot be empty");

    this.notificationStrategy.sendNotification(message);
    console.log("Notification sent successfully!");
  }
}

class MultiNotifier {
  constructor(private notificationStrategies: NotificationStrategy[]) {}

  notify(message: string): void {
    if(!message) throw new Error("Message cannot be empty");

    this.notificationStrategies.forEach(
      (strategy) => strategy.sendNotification(message)
    );
    console.log("All notifications sent successfully!");
  }
}

// Usage

const notifier = new Notifier(new SMSNotification());
notifier.notify("You have a new message!");

const emailNotifier = new Notifier(new EmailNotification());
emailNotifier.notify("Your order has been shipped!");

const pushNotifier = new Notifier(new PushNotification());
pushNotifier.notify("You have a new friend request!");