package Assignment1;

import java.util.*;;

public class Main {
    public static void main(String[] args) {
        List<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i < 4; i++) {
            jobs.add(Utilities.userInput());
        }
        FirstComeFirstServe scheduler = new FirstComeFirstServe(jobs);
        scheduler.processJobs();
    }
}

class FirstComeFirstServe {
    List<Job> jobs;

    FirstComeFirstServe(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, new ArrivalComparator());
    }

    void processJobs() {
        LinkedList<Job> queue = new LinkedList<Job>();
        for (Job job : this.jobs) {
            queue.add(job);
        }
        int t = 0;
        Job currentJob = null;
        List<Job> completedJobs = new ArrayList<Job>();
        while (!queue.isEmpty()) {
            currentJob = queue.remove();
            t = t > currentJob.arrivalTime ? t : currentJob.arrivalTime;
            currentJob.startTime = t;
            currentJob.responseTime = t;
            currentJob.waitingTime = t - currentJob.arrivalTime;
            t += currentJob.busTime;
            currentJob.busLeft = 0;
            currentJob.endTime = t;
            completedJobs.add(currentJob);
            currentJob = null;
        }
        Utilities.show(completedJobs);
    }
}

class ShortestJobFirst {
    List<Job> jobs;
}

class Utilities {
    static Scanner sc = new Scanner(System.in);
    static Job userInput() {
        String name = sc.next();
        int arrivalTime = sc.nextInt();
        int busTime = sc.nextInt();
        int priority = sc.nextInt();
        sc.reset();
        Job newJob = new Job(name, arrivalTime, busTime, priority);
        return newJob;
    }

    static void show(List<Job> jobs) {
        for (Job job : jobs) {
            System.out.println(job);
        }
    }
}

class Job implements Comparable<Job> {
    String name;
    int arrivalTime;
    int busTime;
    int priority;
    int startTime;
    int endTime;
    int responseTime;
    int waitingTime;
    int busLeft;

    Job(String name, int arrivalTime, int busTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.busTime = busTime;
        this.priority = priority;
        this.startTime = -1;
        this.endTime = -1;
        this.responseTime = -1;
        this.waitingTime = 0;
        this.busLeft = busTime;
    }

    @Override
    public String toString() {
        return String.format("%s:\t%d\t%d\t%d\t%d\t%d\t%d\t%d", name, arrivalTime, busTime, priority, startTime, endTime, responseTime, waitingTime);
    }

    @Override
    public int compareTo(Job o) {
        return o.arrivalTime;
    }
}

class ArrivalComparator implements Comparator<Job> {

    @Override
    public int compare(Job a, Job b) {
        return a.arrivalTime - b.arrivalTime;
    }
    
}

