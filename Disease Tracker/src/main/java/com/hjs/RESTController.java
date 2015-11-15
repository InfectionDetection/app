package com.hjs;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.hjs.db.obj.GCM;
import com.hjs.db.obj.History;
import com.hjs.db.obj.Track;
import com.hjs.db.obj.User;
import com.hjs.db.repo.GCMRepository;
import com.hjs.db.repo.HistoryRepository;
import com.hjs.db.repo.TrackRepository;
import com.hjs.db.repo.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saswat on 11/14/2015.
 */
@RestController
public class RESTController {

    public static final String GCM_API_KEY = "AIzaSyBDr7WTGq0cZEZ1Uw2aShfGFqX97Eo-54k";

    @Autowired UserRepository userRepository;
    @Autowired TrackRepository trackRepository;
    @Autowired HistoryRepository historyRepository;
    @Autowired GCMRepository gcmRepository;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value="uuid", required=true) String uuid) {

        userRepository.save(new User(uuid));

        return "saved";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@RequestParam(value="uuid", required=true) String uuid,
                           @RequestParam(value="sick", required=true) String sick) {
        int sick_i = Integer.parseInt(sick);

        User u = userRepository.findByUuid(uuid);
        if(sick_i == 0) {//no longer sick
            if(u.getSickStart() == null) {
                return "not saved";
            }
            historyRepository.save(new History(u, u.getSickStart(), new Date()));

            u.setSickStart(null);
            u.setStatus(Integer.parseInt(sick));
        } else {
            u.setSickStart(new Date());
            u.setStatus(Integer.parseInt(sick));
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    User u = userRepository.findByUuid(uuid);
                    GCM g = gcmRepository.findByUser(u);
                    if(g != null && u.getStatus() != 0) {
                        message("timeout", g.getRegistration());
                    }
                }
            }, 60, TimeUnit.SECONDS);
            for(User s : userRepository.findAll()) {
                GCM g = gcmRepository.findByUser(s);
                if(g != null && u.getStatus() == 0) {
                    message("notify", g.getRegistration());
                }
            }
        }
        userRepository.save(u);

        return "saved";
    }

    @RequestMapping(value = "/allowtrack", method = RequestMethod.POST)
    public String allowTrack(@RequestParam(value="uuid", required=true) String uuid,
                             @RequestParam(value="allow", required=true) String allow) {

        User u = userRepository.findByUuid(uuid);
        if(allow.equals("true")) {
            u.setTrack(1);
            //todo check if scheduled already
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    User u = userRepository.findByUuid(uuid);
                    GCM g = gcmRepository.findByUser(u);
                    if(g != null && u.getTrack() == 1 && u.getStatus() != 0) {
                        message("track", g.getRegistration());
                        scheduler.schedule(this, 10, TimeUnit.SECONDS);
                    }
                }
            }, 1, TimeUnit.SECONDS);
        } else {
            u.setTrack(0);
        }

        userRepository.save(u);

        return "saved";
    }

    @RequestMapping(value = "/track", method = RequestMethod.POST)
    public String track(@RequestParam(value="uuid", required=true) String uuid,
                        @RequestParam(value="lat", required=true) String lat,
                        @RequestParam(value="lng", required=true) String lng) {

        User u = userRepository.findByUuid(uuid);

        trackRepository.save(new Track(u, u.getStatus(), new BigDecimal(lat), new BigDecimal(lng)));

        return "saved";
    }

    @RequestMapping(value = "/gcm", method = RequestMethod.POST)
    public String gcm(@RequestParam(value="uuid", required=true) String uuid,
                        @RequestParam(value="gcm", required=true) String gcm) {

        User u = userRepository.findByUuid(uuid);
        if(u == null) {
            register(uuid);
            return gcm(uuid, gcm);
        }

        gcmRepository.save(new GCM(u, gcm));

        return "saved";
    }

    private void message(String message, String reg) {
        final int retries = 3;
        final String notificationToken = reg;
        Sender sender = new Sender(GCM_API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();

        try {
            Result result = sender.send(msg, notificationToken, retries);

            if (StringUtils.isEmpty(result.getErrorCodeName())) {
                System.err.println("Error occurred while sending push notification :" + result.getErrorCodeName());
            } else {
                System.out.println("GCM Notification is sent successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/points", method = RequestMethod.GET)
    public String points(@RequestParam(value="lat", required=true) String lat,
                         @RequestParam(value="lng", required=true) String lng,
                         @RequestParam(value="rad", required=true) String rad,
                         @RequestParam(value="time", required=true) String time) {
        BigDecimal latitude = new BigDecimal(lat);
        BigDecimal longtitude = new BigDecimal(lng);
        BigDecimal radius2 = new BigDecimal(rad).pow(2);

        Date u = new Date(Long.parseLong(time));
        Date d = new Date(u.getTime()-432000000L);
        ArrayList<Track> tracks = new ArrayList<>();

        for(Track t : trackRepository.findAll()) {//InRange(new Date(d.getTime()+432000000L), d)) {
            System.out.print("Tracks:"+t);
            if(t.getDateCreated().getTime() < u.getTime() && t.getDateCreated().getTime() > d.getTime() && t.getLat().subtract(latitude).pow(2).add(t.getLng().subtract(longtitude).pow(2)).compareTo(radius2) <= 0) {
                tracks.add(t);
            }
        }

        JSONArray arr = new JSONArray();
        for(Track t : tracks) {
            JSONObject obj = new JSONObject();
            obj.put("lat", t.getLat().toString());
            obj.put("lng", t.getLng().toString());
            arr.add(obj);
        }

        return arr.toJSONString();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String status(@RequestParam(value="uuid", required=true) String uuid) {
        return userRepository.findByUuid(uuid).getStatus()+"";
    }

}
