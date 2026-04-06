interface NotificationStrategy {
  void sendNotification(String message);
}

class EmailNotification implements NotificationStrategy {
  @override
  void sendNotification(String message) {
    System.out.println("Sending email notification: $message");
  }
}

class SMSNotification implements NotificationStrategy {
  @override
  void sendNotification(String message) {
    System.out.println("Sending SMS notification: $message");
  }
}

class PushNotification implements NotificationStrategy {
  @override
  void sendNotification(String message) {
    System.out.println("Sending push notification: $message");
  }
}

class Notifier {
  private NotificationStrategy notificationStrategy;

  constructor(NotificationStrategy strategy) {
    this.notificationStrategy = strategy;
  }

  notify(string message) {
    if(message == null || message.isEmpty()) {
      throw new IllegalArgumentException("Message cannot be null or empty.");
    }

    notificationStrategy.sendNotification(message);
  }
}

class MultiNotifier {
  private List<NotificationStrategy> notificationStrategies;

  constructor(List<NotificationStrategy> strategies) {
    this.notificationStrategies = strategies;
  }

  notifyAll(string message) {
    if(message == null || message.isEmpty()) {
      throw new IllegalArgumentException("Message cannot be null or empty.");
    }

    for(NotificationStrategy strategy : this.notificationStrategies) {
      strategy.sendNotification(message);
    }
  }
}

Notifier notyfier = new Notifier(new EmailNotification());
notyfier.notify("Hello, this is an email notification!");

MultiNotifier multiNotifier = new MultiNotifier(
  Arrays.asList(new EmailNotification(), 
                new SMSNotification(), 
                new PushNotification())
);