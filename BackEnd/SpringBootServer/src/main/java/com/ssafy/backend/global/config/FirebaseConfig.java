package com.ssafy.backend.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Firebase 서비스 설정을 관리하는 구성 클래스입니다.
 * 이 클래스는 FirebaseApp 인스턴스를 초기화하여 Firebase 서비스에 연결합니다.
 */
@Configuration
public class FirebaseConfig {
    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    @Value("${app.firebase-project-id}")
    private String projectId;

    private final ApplicationContext applicationContext;

    /**
     * FirebaseConfig 클래스의 생성자입니다.
     *
     * @param applicationContext Spring 애플리케이션 컨텍스트로, Firebase 설정 파일을 로드하는 데 사용됩니다.
     */
    public FirebaseConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Firebase 애플리케이션을 초기화합니다.
     * 이 메소드는 스프링 라이프사이클에 의해 자동으로 호출됩니다.
     */
    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = createFirebaseOptions();
            initializeFirebaseApp(options);
        } catch (IOException e) {
            throw new IllegalStateException("Firebase 초기화 실패", e);
        }
    }

    /**
     * FirebaseOptions 객체를 생성합니다.
     *
     * @return FirebaseOptions 생성된 Firebase 옵션
     * @throws IOException 구성 파일을 읽는 동안 발생하는 예외
     */
    private FirebaseOptions createFirebaseOptions() throws IOException {
        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(applicationContext.getResource(firebaseConfigPath).getInputStream()))
                .setProjectId(projectId)
                .build();
    }

    /**
     * FirebaseApp을 초기화합니다. 이미 초기화된 경우 무시됩니다.
     *
     * @param options Firebase 앱을 초기화하는데 사용되는 옵션
     */
    private void initializeFirebaseApp(FirebaseOptions options) {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
