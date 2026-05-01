# Career Roadmap: PHP → Spring Boot at a Good Firm

> [!NOTE]
> This is **brutally honest** advice. No sugarcoating. You asked what to do, so here it is.

---

## 1. The Hard Truth First

**You're not old.** 25 is fine. Many people switch stacks at 27-30 and land great roles. Stop panicking about age — it's a distraction from actual work.

**The real problem isn't your age or college. It's this:**
- Your projects have the right *keywords* but a hiring manager at a good firm will sniff out AI-generated code in 5 minutes during a technical round.
- Your CV reads like it was optimized for ATS bots, not humans. Every bullet point sounds like a ChatGPT prompt response. Real engineers don't write "Engineered a Dynamic Risk-Based Interest Engine" on their CV — they write "Built interest rate calculation based on credit score tiers."

**The good news:** Your project *choices* are actually smart. Financial systems and healthcare/queue management are real-world domains that interviewers respect. The foundation is there. You just need to go deeper.

---

## 2. Honest CV Feedback

### What's Working ✅
| Aspect | Why It Works |
|--------|-------------|
| Domain choices | Financial ledger + healthcare queue = mature, real-world problems |
| Tech stack alignment | Spring Boot, JPA, Kafka, WebSockets, JWT — these are exactly what firms want |
| Professional experience | The Trogon experience bullets about legacy code and async notifications are your **strongest points** |

### What's Not Working ❌

| Problem | Example from Your CV | Fix |
|---------|---------------------|-----|
| **Buzzword overload** | "Engineered a Dynamic Risk-Based Interest Engine" | → "Built interest rate calculation that adjusts based on user credit score" |
| **Vague quantification** | "100% auditability" — what does that even mean? | → "Every wallet transaction creates an immutable ledger entry for audit trail" |
| **AI-generated phrasing** | "Optimized for High Concurrency: Leveraged Pessimistic Locking" | → "Used SELECT FOR UPDATE to prevent double-payments during concurrent requests" |
| **Too many projects** | 3 projects that all blur together | → Lead with 2, make them **deep** |
| **No results/impact** | Nothing about what you learned or problems you hit | → Add a "Key Challenge" line showing you debugged something hard |
| **Missing: Tests** | No mention of actual test coverage | → Write tests, then mention them |

### Rewritten Experience Section (Example)

```
Junior Backend Developer | Trogon Media Pvt. Ltd.                    June 2025 – Present

• Maintained a large legacy PHP codebase; mapped module dependencies to assess 
  impact of changes before implementation, reducing regression incidents.
• Built async notification system for appointment booking using queue-based 
  architecture (producer-consumer pattern) to prevent request blocking.
• Solved a production cronjob overlap bug using file-based atomic locking, 
  preventing duplicate job execution across multiple workers.
```

> [!IMPORTANT]
> **Your Trogon experience is your biggest asset.** Production experience with legacy code, 
> debugging real bugs, and solving concurrency issues — this is what separates you from 
> freshers with tutorial projects. **Lead with this. Expand it. Own it.**

---

## 3. Honest Project Assessment

### What I Found in Your Code

I reviewed both `appointment_system` and `loanLedger`. Here's the truth:

#### MedQueue (Appointment System)
| Aspect | Assessment |
|--------|-----------|
| **Architecture** | Clean layered structure, proper separation ✅ |
| **Security** | JWT + role-based access properly configured ✅ |
| **WebSockets** | Real-time queue updates work — this is genuinely interesting ✅ |
| **Concurrency** | `ReentrantLock` in QueueShufflingService — good concept ⚠️ but single-instance only |
| **ThreadPool in AppointmentService** | Static `BlockingQueue` + raw `ExecutorService` fields smell like "I added this to put it on my CV" ⚠️ |
| **Insurance check** | `CompletableFuture` that saves inside `thenAccept` while the outer `@Transactional` continues — **this is a race condition bug** ❌ |
| **Tests** | Zero test files found ❌ |

#### LoanLedger
| Aspect | Assessment |
|--------|-----------|
| **Domain modeling** | Loan → Installment → LedgerEntry → Wallet pipeline is solid ✅ |
| **Kafka integration** | Event-driven credit check decoupling is a great architectural choice ✅ |
| **AOP Auditing** | Custom annotation + aspect for audit logging — demonstrates Spring internals knowledge ✅ |
| **BigDecimal usage** | Correct for financial calculations ✅ |
| **Tests** | Zero test files found ❌ |

> [!WARNING]
> ### The AI Smell Test
> An interviewer will ask: *"Walk me through what happens when two users try to pay the same installment simultaneously."*
> 
> If you can't explain your pessimistic locking flow **from memory**, without looking at code, they'll know you didn't write it. **You need to understand every line.**

---

## 4. The 90-Day Action Plan

### Phase 1: Weeks 1-3 — "Own Your Code"

**Goal:** Be able to explain every line of your existing projects without looking at code.

- [ ] **Fix the race condition** in `AppointmentService.bookAppointment()` — the `CompletableFuture.thenAccept` runs on a different thread outside the `@Transactional` boundary. This is a real bug. Fix it and understand *why* it's a bug.
- [ ] **Write tests.** This is non-negotiable. Good firms will ask about testing.
  - Write unit tests for `LoanService`, `InstallmentService`, `WalletService` using JUnit 5 + Mockito
  - Write integration tests for the appointment booking flow
  - Aim for at least 15-20 meaningful tests per project
- [ ] **Draw the architecture** of both projects on paper. Sequence diagrams of key flows. If you can't draw it, you don't understand it.
- [ ] **Understand Spring internals** that your code uses:
  - How does `@Transactional` actually work? (Proxy-based AOP)
  - How does Spring Security filter chain execute?
  - How does `@Async` work under the hood?
  - What happens if your Kafka consumer throws an exception?

### Phase 2: Weeks 4-6 — "Add Real Depth"

**Goal:** Add features that demonstrate you understand production concerns.

- [ ] **Add proper exception handling tests** — test that your `@ControllerAdvice` returns correct HTTP codes
- [ ] **Add database migration** using Flyway or Liquibase (every good firm uses this)
- [ ] **Add request rate limiting** — use Bucket4j or a simple filter-based approach
- [ ] **Add health checks** and basic metrics using Spring Actuator
- [ ] **Dockerize properly** — multi-stage build, health checks in docker-compose
- [ ] **Write a proper README** — setup instructions, API docs, architecture diagram, design decisions
- [ ] **Deploy one project** — even a free-tier Railway/Render deployment shows initiative

### Phase 3: Weeks 7-12 — "Interview Grind"

**Goal:** Be ready for Spring Boot interviews at good firms.

#### Core Java (asked in EVERY interview)
| Topic | What to Know |
|-------|-------------|
| Collections | HashMap internals, ConcurrentHashMap, when to use which List |
| Multithreading | Thread lifecycle, ExecutorService, CompletableFuture, volatile vs synchronized |
| Java 8+ | Streams (collect, reduce, groupBy), Optional, functional interfaces |
| Memory | Stack vs heap, garbage collection basics, memory leaks |
| OOP | SOLID principles with *your own code examples* |

#### Spring Boot (asked at Java firms)
| Topic | What to Know |
|-------|-------------|
| IoC/DI | How Spring creates beans, bean scopes, circular dependencies |
| Spring Security | Filter chain flow, JWT validation flow, custom filters |
| JPA/Hibernate | N+1 problem, lazy vs eager, `@Transactional` propagation levels |
| REST | HTTP methods, status codes, idempotency, pagination |
| Testing | `@SpringBootTest` vs `@WebMvcTest` vs `@DataJpaTest` |

#### System Design (for better firms)
| Topic | What to Know |
|-------|-------------|
| Database | Indexing, normalization, read replicas |
| Caching | When to cache, invalidation strategies, Redis basics |
| Message Queues | Kafka vs RabbitMQ, exactly-once delivery, dead letter queues |
| API Design | Versioning, rate limiting, circuit breaker pattern |

---

## 5. Job Search Strategy

### Where to Apply

| Tier | Firms | Realistic? |
|------|-------|-----------|
| **Product companies (mid)** | Zoho, Freshworks, Chargebee, Postman, Razorpay | Yes, with prep |
| **Good service firms** | Thoughtworks, Hashedin, Publicis Sapient | Yes, most accessible |
| **Startups (Series A-C)** | Check AngelList/Wellfound for Spring Boot roles | Yes, best chance |
| **Big tech** | Google, Amazon, Microsoft | Not yet — need DSA + more experience |

### How to Beat the "College + Firm" Prejudice

1. **GitHub commits matter.** Consistent, meaningful commits over 3+ months show discipline. Not just "added feature" — show PR-quality commit messages, branches, and code reviews.

2. **LinkedIn content.** Write 2-3 short posts about what you learned:
   - "I found a race condition in my own code — here's what happened"
   - "How @Transactional actually works in Spring (it's not what you think)"
   - These get you noticed by hiring managers who value learning ability.

3. **Open source contributions.** Even small ones. Fix a typo in Spring Boot docs. Submit a bug report to a library you use. This shows you engage with the ecosystem.

4. **Referrals > Cold applications.** Connect with engineers at target companies on LinkedIn. Not "please refer me" — actually engage with their content first.

5. **Don't mention AI tools built your projects.** But be ready to explain every design decision as if you made it. This means you **must** understand the code deeply.

---

## 6. What NOT to Do

- ❌ Don't start a 3rd project. Deepen the 2 you have.
- ❌ Don't learn microservices/Docker/Kubernetes superficially. Go deep on Spring Boot fundamentals first.
- ❌ Don't apply to 200 jobs with the same CV. Customize for each role.
- ❌ Don't panic about being 25. Waste of mental energy.
- ❌ Don't keep adding AI-generated features you can't explain. It will backfire in interviews.

---

## 7. Priority Stack (What to Learn, In Order)

```
1. Core Java deep dive (Collections, Concurrency, Streams)     ← MOST ASKED
2. Spring Boot internals (not just usage, but HOW it works)     ← DIFFERENTIATOR  
3. JPA/Hibernate (N+1, caching, dirty checking)                ← COMMON TRAP
4. Testing (JUnit 5 + Mockito + Integration tests)             ← SHOWS MATURITY
5. SQL (joins, indexing, query optimization)                    ← ALWAYS ASKED
6. System Design basics                                         ← FOR BETTER FIRMS
7. DSA (LeetCode medium level, ~100 problems)                  ← GATE KEEPER
```

---

> [!TIP]
> ### The One Thing That Will Actually Get You Hired
> 
> It's not your college. It's not your current firm. It's **being able to have a 30-minute 
> technical conversation about your own code** — explaining trade-offs, bugs you found, 
> things you'd do differently, and why you chose X over Y.
> 
> **Go read your own code line by line. Understand it like water. That's your path.**
