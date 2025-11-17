# 게시판 서비스 구현

## 기술 스택
- Backend: Java, Spring Boot, Spring Security, JPA, H2 db
  
- Frontend: Thymeleaf , HTML/CSS , JavaScript
       

## 실행 방법
```
http://localhost:8080 (애플리케이션 접속)
```

```
http://localhost:8080/h2-console (H2 콘솔 접속)
```
```
JDBC URL: jdbc:h2:mem:boarddb
Username: sa
Password: (공백)
```


## ERD

<img width="1740" height="662" alt="board_erd" src="https://github.com/user-attachments/assets/a013a54e-3794-4b87-b817-f6b341ee0782" />

### User
- 회원 정보 저장
- username은 UNIQUE 제약조건

### Board
- 게시글 정보 저장
- user_id로 작성자와 연결

### Comment
- 댓글 정보 저장
- mparent_id로 계층 구조 구현 (자기참조)
- NULL이면 최상위 댓글, 값이 있으면 대댓글

### BoardLike
- 좋아요 정보 저장
- 복합 UNIQUE(board_id, user_id)로 중복 방지

## API

### 게시글 API

| Method | URL | 설명 | 인증필요 |
| --- | --- | --- |--- |  
| GET | /boards | 게시글 목록 조회 | X | 
| GET | /boards/{id} | 게시글 상세 조회 | X | 
| GET | /boards/new | 게시글 작성 폼| O | 
| POST | /boards/new | 게시글 작성 | O | 
| GET | /boards/{id}/edit | 게시글 수정 폼| O | 
| POST | /boards/{id}/edit | 게시글 수정 | O | 
| POST | /boards/{id}/delete | 게시글 삭제 | O | 


### 좋아요 API

| Method | URL | 설명 | 인증필요 |
| --- | --- | --- |--- |  
| POST | /api/boards/{id}/like | 좋아요 토글 | O | 


### 댓글 API

| Method | URL | 설명 | 인증필요 |
| --- | --- | --- |--- |  
| POST | /api/comments | 댓글 작성 | O | 
| PUT | /api/comments/{id} | 댓글 수정 | O | 
| DELETE | /api/comments/{id} | 댓글 삭제 | O | 


### 인증 API

| Method | URL | 설명 | 인증필요 |
| --- | --- | --- |--- |  
| GET | /login | 로그인 페이지 | X | 
| POST | /login | 로그인 처리 | X | 
| GET | /register| 회원가입 페이지 | X | 
| POST | /register | 회원가입 처리 | X | 
| POST | /logout | 로그아웃 | O | 


## 구현 기능

### 인증 (Spring Security) (회원가입 및 로그인)
![회원가입성공](https://github.com/user-attachments/assets/143c5325-10b2-4340-8c24-5a0afcb4ea99)
회원가입 성공

<img width="1126" height="439" alt="회원db저장성공" src="https://github.com/user-attachments/assets/fec3130e-165b-41c8-a8cf-1d1414c69bd8" />
회원 DB 저장


![회원가입실패중복](https://github.com/user-attachments/assets/6dcedb4f-1793-4eea-b6cd-f68d50ab5b23)
회원가입 실패 (중복)


![로그인성공](https://github.com/user-attachments/assets/da83dbac-0820-4fb9-af8d-8b49d7ad5936)
로그인 성공


![로그인실패](https://github.com/user-attachments/assets/89b1cbe0-40f8-478e-8945-f5362bb0b40e)
로그인 실패




#### SecurityConfig
- FormLogin 방식 사용
- CSRF 보호 활성화 (CookieCsrfTokenRepository)
- BCrypt 비밀번호 암호화

```
@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/h2-console/**") 
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/boards", "/boards/*", "/login", "/register", "/css/**", "/h2-console/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/boards")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/boards")
                        .permitAll()
                )
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }
```

#### CustomUserDetailsService
- Spring Security와 JPA 연동
- DB에서 사용자 정보 조회

---

### 게시글
![게시판글등록및좋아요](https://github.com/user-attachments/assets/ed79b32f-c78a-4fe8-945f-afbfa680109f)
게시글 등록 & 좋아요

![다른계정게시글작성](https://github.com/user-attachments/assets/b26307da-69fe-4ce8-b077-e3d90a6caa90)
다른 계정으로 게시글 작성

<img width="1222" height="937" alt="게시글db저장" src="https://github.com/user-attachments/assets/693c610b-2100-4976-b3b8-234ff6851c0c" />
게시글 DB 저장


#### 페이징

![페이징](https://github.com/user-attachments/assets/83cabcb7-1895-4c04-bb85-2e811414449f)

- Spring Data JPA Pageable 사용
- 사용자한테 size를 입력받아 size만큼씩 표시
```
@GetMapping("/boards")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Authentication auth, Model model) {
        Page<Board> boards = boardService.getList(page, size); // 컨트롤러에서 데이터 받음



public Page<Board> getList(int page, int size) {
        return boardRepository.findAll(PageRequest.of(page, size));
    } // Pageable 생성
```

#### 권한

![본인게시글만수정삭제버튼](https://github.com/user-attachments/assets/19d78a82-507d-4bd7-a115-b397caaec730)

- 작성자 본인만 수정/삭제 가능
```
@Transactional
    public void update(Long id, String title, String content, User user) {
        Board board = boardRepository.findById(id).orElseThrow();
        if (!board.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다");
        }
        board.setTitle(title);
        board.setContent(content);
    }

    @Transactional
    public void delete(Long id, User user) {
        Board board = boardRepository.findById(id).orElseThrow();
        if (!board.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("권한이 없습니다");
        }
        boardRepository.delete(board);
    }
```

---

### 댓글

![댓글및답글달기](https://github.com/user-attachments/assets/1819c0eb-23f1-4491-bfb9-8828828fa0b8)
댓글 작성

![댓글수정](https://github.com/user-attachments/assets/f03f149c-0d24-4133-aac7-9943aaf309d6)
댓글 수정

![다른사람댓글](https://github.com/user-attachments/assets/4aaa827c-a18e-4ddb-b812-1bf33b95a9e0)
다른 계정으로 댓글 작성

<img width="947" height="607" alt="댓글db" src="https://github.com/user-attachments/assets/7189efc0-4b8c-47cd-b691-48b3da4a476a" />
댓글 DB 저장


- 자기참조 구조 ( Comment 엔티티 parent_id로 구현 )
- parentId가 null이면 댓글, parentId가 있으면 대댓글
- 최상위 댓글이 삭제되면 자식 댓글도 삭제됨
- 무한 depth 지원 ( 이 프로젝트에서는 2개까지만 출력되게 했습니다. )


```
 @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

```


| id | content | parent_id |
| --- | --- | --- |
| 1 | 댓글 | NULL (최상위) |
| 2 | 대댓글 | 1 (댓글의 자식)|
| 3 | 대대댓글 | 2 (대댓글의 자식)|

---

### 좋아요

#### 중복 방지
- 복합 유니크 제약조건
```
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "user_id"}))
```

#### 토글방식
- 이미 좋아요가 있으면 삭제, 없으면 추가
- Board 엔티티에 likeCount 저장 (비정규화)
- COUNT 쿼리 없이 빠르게 조회 가능
```
 @Transactional
    public void toggle(Long boardId, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow();

        BoardLike like = likeRepository.findByBoardIdAndUserId(boardId, user.getId())
                .orElse(null);

        if (like != null) {
            // 좋아요 취소
            likeRepository.delete(like);
            board.setLikeCount(board.getLikeCount() - 1);
        } else {
            // 좋아요 추가
            BoardLike newLike = new BoardLike();
            newLike.setBoard(board);
            newLike.setUser(user);
            likeRepository.save(newLike);
            board.setLikeCount(board.getLikeCount() + 1);
        }
    }
```

---

### AJAX 통신

#### CSRF 토큰 처리
- 모든 POST/PUT/DELETE 요청에 토큰 포함
```
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

 fetch('/api/comments', {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            }
```

