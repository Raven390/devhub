package ru.devhub.web.application.user.register;

/**
 * Команда для регистрации нового пользователя.
 * <p>
 * Используется в application-слое для передачи данных между внешним API и сервисом регистрации.
 * Инкапсулирует все параметры, необходимые для создания новой учётной записи пользователя.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>email</b> — адрес электронной почты пользователя (уникален, не null, проверяется на корректность и уникальность).</li>
 *   <li><b>name</b> — имя пользователя (отображается в профиле, не null, требования к длине и формату определяются бизнес-логикой).</li>
 *   <li><b>password</b> — пароль пользователя (не хранится в открытом виде, валидация на сложность и длину осуществляется в домене/сервисе).</li>
 * </ul>
 *
 * <b>Безопасность:</b>
 * <ul>
 *   <li>Пароль должен быть захеширован до хранения.</li>
 *   <li>Email — основа для аутентификации, возможны проверки на подтверждение и уникальность.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * RegisterUserCommand cmd = new RegisterUserCommand("user@example.com", "User Name", "securePassword123");
 * User user = registerUserService.handle(cmd);
 * </pre>
 *
 * @see RegisterUserService
 */
public record RegisterUserCommand(String email, String name, String password) {}