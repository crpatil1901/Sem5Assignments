package Assignment1;

import java.util.*;

class Job {
    String name;
    int arrivalTime;
    int priority;
    int busTime;
    int remainingTime;
    List<Integer> allocatedTimes;
    int startTime;
    int endTime;
    int waitTime;

    Job(String name, int arrivalTime, int priority, int busTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.busTime = busTime;
        this.remainingTime = busTime;
        this.allocatedTimes = new ArrayList<>();
        this.startTime = -1;
        this.endTime = -1;
        this.waitTime = 0;
    }

    void allocateTime(int time) {
        allocatedTimes.add(time);
    }
}

class FirstComeFirstServe {
    List<Job> jobs;

    FirstComeFirstServe(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, Comparator.comparingInt(job -> job.arrivalTime));
    }

    void processJobs() {
        int currentTime = 0;
        List<Job> completedJobs = new ArrayList<>();

        for (Job job : jobs) {
            currentTime = Math.max(currentTime, job.arrivalTime);
            job.startTime = currentTime;
            job.waitTime = currentTime - job.arrivalTime;

            for (int t = 0; t < job.busTime; t++) {
                job.allocatedTimes.add(currentTime + t);
            }

            currentTime += job.busTime;
            job.endTime = currentTime;
            completedJobs.add(job);
        }

        Main.displayResults(completedJobs);
    }
}

class NonPreemptivePriority {
    List<Job> jobs;

    NonPreemptivePriority(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, (job1, job2) -> {
            if (job1.arrivalTime == job2.arrivalTime) {
                return job1.priority - job2.priority; // Sort by priority if arrival times are the same
            }
            return job1.arrivalTime - job2.arrivalTime; // Sort by arrival time
        });
    }

    void processJobs() {
        int currentTime = 0;
        List<Job> completedJobs = new ArrayList<>();
        PriorityQueue<Job> jobQueue = new PriorityQueue<>(Comparator.comparingInt(job -> job.priority));

        while (!jobs.isEmpty() || !jobQueue.isEmpty()) {
            while (!jobs.isEmpty() && jobs.get(0).arrivalTime <= currentTime) {
                jobQueue.add(jobs.remove(0));
            }

            if (!jobQueue.isEmpty()) {
                Job currentJob = jobQueue.poll();

                if (currentJob.startTime == -1) {
                    currentJob.startTime = currentTime;
                }

                for (int t = 0; t < currentJob.busTime; t++) {
                    currentJob.allocateTime(currentTime + t); // Allocate each second the job runs
                }

                currentTime += currentJob.busTime;
                currentJob.endTime = currentTime;
                completedJobs.add(currentJob);
            } else {
                currentTime++;
            }
        }

        Main.displayResults(completedJobs);
    }
}


class PreemptiveShortestJobFirst {
    List<Job> jobs;

    PreemptiveShortestJobFirst(List<Job> jobs) {
        this.jobs = jobs;
        Collections.sort(jobs, Comparator.comparingInt(job -> job.arrivalTime));
    }

    void processJobs() {
        int currentTime = 0;
        List<Job> completedJobs = new ArrayList<>();
        PriorityQueue<Job> jobQueue = new PriorityQueue<>(Comparator.comparingInt(job -> job.remainingTime));

        while (!jobs.isEmpty() || !jobQueue.isEmpty()) {
            while (!jobs.isEmpty() && jobs.get(0).arrivalTime <= currentTime) {
                jobQueue.add(jobs.remove(0));
            }

            if (!jobQueue.isEmpty()) {
                Job currentJob = jobQueue.poll();
                int burstTime = Math.min(currentJob.remainingTime, 1);

                if (currentJob.startTime == -1) {
                    currentJob.startTime = currentTime;
                }

                for (int t = 0; t < burstTime; t++) {
                    currentJob.allocateTime(currentTime + t); // Allocate each second the job runs
                }

                currentJob.remainingTime -= burstTime;
                currentTime += burstTime;

                if (currentJob.remainingTime == 0) {
                    currentJob.endTime = currentTime;
                    completedJobs.add(currentJob);
                } else {
                    jobQueue.add(currentJob);
                }
            } else {
                currentTime++;
            }
        }

        Main.displayResults(completedJobs);
    }
}

class PreemptiveRoundRobin {
    List<Job> jobs;
    int timeQuantum;

    PreemptiveRoundRobin(List<Job> jobs, int timeQuantum) {
        this.jobs = jobs;
        this.timeQuantum = timeQuantum;
        Collections.sort(jobs, Comparator.comparingInt(job -> job.arrivalTime));
    }

    void processJobs() {
        int currentTime = 0;
        List<Job> completedJobs = new ArrayList<>();
        LinkedList<Job> jobQueue = new LinkedList<>();

        while (!jobs.isEmpty() || !jobQueue.isEmpty()) {
            while (!jobs.isEmpty() && jobs.get(0).arrivalTime <= currentTime) {
                jobQueue.add(jobs.remove(0));
            }

            if (!jobQueue.isEmpty()) {
                Job currentJob = jobQueue.poll();
                int burstTime = Math.min(currentJob.remainingTime, timeQuantum);

                if (currentJob.startTime == -1) {
                    currentJob.startTime = currentTime;
                }

                for (int t = 0; t < burstTime; t++) {
                    currentJob.allocateTime(currentTime + t); // Allocate each second the job runs
                }

                currentJob.remainingTime -= burstTime;
                currentTime += burstTime;

                if (currentJob.remainingTime <= 0) {
                    currentJob.endTime = currentTime;
                    completedJobs.add(currentJob);
                } else {
                    jobQueue.add(currentJob);
                }
            } else {
                currentTime++;
            }
        }

        Main.displayResults(completedJobs);
    }
}

public class Main {

    static List<Job> getJobs() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job("Job1", 0, 3, 10));
        jobs.add(new Job("Job2", 1, 2, 5));
        jobs.add(new Job("Job3", 2, 1, 8));
        jobs.add(new Job("Job4", 3, 4, 3));
        return jobs;
    }
    public static void main(String[] args) {
        
        List<Job> jobs = getJobs();

        System.out.println("First-Come-First-Serve (FCFS):");
        FirstComeFirstServe fcfsScheduler = new FirstComeFirstServe(new ArrayList<>(jobs));
        fcfsScheduler.processJobs();

        jobs = getJobs();

        System.out.println("\nNon-preemptive Priority Scheduling:");
        NonPreemptivePriority npPriorityScheduler = new NonPreemptivePriority(new ArrayList<>(jobs));
        npPriorityScheduler.processJobs();

        jobs = getJobs();

        System.out.println("\nPreemptive Shortest Job First (SJF):");
        PreemptiveShortestJobFirst sjfScheduler = new PreemptiveShortestJobFirst(new ArrayList<>(jobs));
        sjfScheduler.processJobs();

        jobs = getJobs();

        int timeQuantum = 3;
        System.out.println("\nPreemptive Round-Robin Scheduling with time quantum " + timeQuantum + ":");
        PreemptiveRoundRobin rrScheduler = new PreemptiveRoundRobin(new ArrayList<>(jobs), timeQuantum);
        rrScheduler.processJobs();
    }

    static void displayResults(List<Job> jobs) {
        System.out.println("Name\tAT\tPriority\tBT\tStart\tEnd\tWait\tAllocated Times");
        for (Job job : jobs) {
            int totalWait = job.endTime - job.arrivalTime - job.busTime;
            System.out.print(job.name + "\t" + job.arrivalTime + "\t" + job.priority + "\t\t" + job.busTime + "\t" + job.startTime + "\t" + job.endTime + "\t" + totalWait + "\t");
            System.out.println(job.allocatedTimes);
        }
    }
}
