package Assignment1;

import java.util.*;;

public class Main {
    public static void main(String[] args) {
        // Create a list of hardcoded jobs
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job("Job1", 0, 10, 3));
        jobs.add(new Job("Job2", 1, 5, 2));
        jobs.add(new Job("Job3", 2, 8, 1));
        jobs.add(new Job("Job4", 3, 3, 4));

        System.out.println("First-Come-First-Serve (FCFS):");
        FirstComeFirstServe fcfsScheduler = new FirstComeFirstServe(new ArrayList<>(jobs));
        fcfsScheduler.processJobs();

        System.out.println("\nShortest Job First (SJF):");
        ShortestJobFirst sjfScheduler = new ShortestJobFirst(new ArrayList<>(jobs));
        sjfScheduler.processJobs();

        System.out.println("\nNon-preemptive Priority Scheduling:");
        Priority priorityScheduler = new Priority(new ArrayList<>(jobs));
        priorityScheduler.processJobs();

        int timeQuantum = 3;
        System.out.println("\nPreemptive Round-Robin Scheduling with time quantum " + timeQuantum + ":");
        RoundRobin rrScheduler = new RoundRobin(new ArrayList<>(jobs), timeQuantum);
        rrScheduler.processJobs();
    }
}



class FirstComeFirstServe {
    List<Job> jobs;

    FirstComeFirstServe(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, new ArrivalTimeComparator());
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

    ShortestJobFirst(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, new ArrivalTimeComparator());
    }

    void processJobs() {
        LinkedList<Job> queue = new LinkedList<>();
        int currentTime = 0;
        Job currentJob = null;
        List<Job> completedJobs = new ArrayList<>();

        while (!jobs.isEmpty() || !queue.isEmpty()) {
            while (!jobs.isEmpty() && jobs.get(0).arrivalTime <= currentTime) {
                queue.add(jobs.remove(0));
            }

            if (!queue.isEmpty()) {
                Collections.sort(queue, new BurstTimeComparator());

                currentJob = queue.removeFirst();

                if (currentJob.busTime > 0) {
                    currentJob.busTime--;
                    currentTime++;

                    if (currentJob.busTime > 0) {
                        queue.addFirst(currentJob);
                    } else {
                        currentJob.endTime = currentTime;
                        completedJobs.add(currentJob);
                    }
                } else {
                    currentJob.endTime = currentTime;
                    completedJobs.add(currentJob);
                }
            } else {
                currentTime++;
            }
        }

        Utilities.show(completedJobs);
    }
}

class Priority {
    List<Job> jobs;

    Priority(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, new PriorityComparator());
    }

    void processJobs() {
        LinkedList<Job> queue = new LinkedList<>();
        int currentTime = 0;
        Job currentJob = null;
        List<Job> completedJobs = new ArrayList<>();

        while (!jobs.isEmpty() || !queue.isEmpty()) {
            while (!jobs.isEmpty() && jobs.get(0).arrivalTime <= currentTime) {
                queue.add(jobs.remove(0));
            }

            if (!queue.isEmpty()) {
                currentJob = queue.removeFirst();
                currentJob.startTime = currentTime;
                currentTime += currentJob.busTime;
                currentJob.endTime = currentTime;
                completedJobs.add(currentJob);
            } else {
                currentTime++;
            }
        }

        Utilities.show(completedJobs);
    }
}

class RoundRobin {
    List<Job> jobs;
    int timeQuantum;

    RoundRobin(List<Job> jobs, int timeQuantum) {
        this.jobs = jobs;
        this.timeQuantum = timeQuantum;
        Collections.sort(jobs, new ArrivalTimeComparator());
    }

    void processJobs() {
        LinkedList<Job> queue = new LinkedList<>();
        int currentTime = 0;
        Job currentJob = null;
        List<Job> completedJobs = new ArrayList<>();

        while (!jobs.isEmpty() || !queue.isEmpty()) {
            while (!jobs.isEmpty() && jobs.get(0).arrivalTime <= currentTime) {
                queue.add(jobs.remove(0));
            }

            if (!queue.isEmpty()) {
                currentJob = queue.removeFirst();

                int remainingTime = currentJob.busTime;
                if (remainingTime > timeQuantum) {
                    currentJob.busTime -= timeQuantum;
                    currentTime += timeQuantum;
                    queue.addFirst(currentJob);
                } else {
                    currentTime += remainingTime;
                    currentJob.endTime = currentTime;
                    completedJobs.add(currentJob);
                }
            } else {
                currentTime++;
            }
        }

        Utilities.show(completedJobs);
    }
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

class ArrivalTimeComparator implements Comparator<Job> {

    @Override
    public int compare(Job a, Job b) {
        return a.arrivalTime - b.arrivalTime;
    }
    
}

class BurstTimeComparator implements Comparator<Job> {
    @Override
    public int compare(Job a, Job b) {
        return a.busTime - b.busTime;
    }
}

class PriorityComparator implements Comparator<Job> {
    @Override
    public int compare(Job a, Job b) {
        return a.priority - b.priority;
    }
}