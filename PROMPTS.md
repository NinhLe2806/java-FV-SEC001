# PROMPTS.md

I need help building a Java 21 CLI application for a large CSV advertising dataset. Before writing any code, do these things in order:
1. Restate the problem in your own words.
2. Identify the functional requirements, non-functional requirements, and output artifacts.
3. Point out the performance constraints implied by a ~1GB input file.
4. Propose a minimal architecture for a maintainable solution.
Do not generate code yet.

Given this CSV schema:
- `campaign_id` (string)
- `date` (YYYY-MM-DD)
- `impressions` (integer)
- `clicks` (integer)
- `spend` (decimal)
- `conversions` (integer)

Design the internal data flow for a streaming implementation. I want a clear explanation of:
- how the file should be read
- what should be kept in memory
- what should not be kept in memory
- where aggregation should happen
- where output writing should happen
- where validation and error handling should happen
Keep the design practical for a coding assignment, not enterprise-heavy.

Recommend the exact Maven dependencies for this project and justify each one in one sentence. Separate runtime dependencies from test dependencies. Prefer a small dependency set. Assume Java 21 and Maven.

Generate the initial project scaffold in Java 21 with package `org.example`. Create only the minimal classes needed for a clean first iteration:
- CLI entry point
- application/service layer
- CSV reader
- aggregation model
- output writer
- domain record model
Use naming that would look reasonable in a take-home assignment submission.

Implement only requirement 1 for now:
Aggregate by `campaign_id` and compute:
- `total_impressions`
- `total_clicks`
- `total_spend`
- `total_conversions`
- `CTR = total_clicks / total_impressions`
- `CPA = total_spend / total_conversions`
If conversions are zero, output blank CPA.

Important constraints:
- use a streaming approach
- do not load the full file into memory
- do not over-engineer the solution
- generate code only
- after coding, list the files created or changed and explain the role of each file

Before implementing top-10 outputs, evaluate whether the current aggregation structure is sufficient. If yes, explain why. If not, refactor the aggregation result shape first, but keep the change minimal and justified.

Now implement these additional outputs:
1. `campaign_summary.csv`
2. `top10_ctr.csv`
3. `top10_cpa.csv`

Rules:
- `top10_ctr.csv` must contain the 10 campaigns with highest CTR
- `top10_cpa.csv` must contain the 10 campaigns with lowest CPA
- campaigns with zero conversions must be excluded from `top10_cpa.csv`
- output columns must be deterministic and consistently formatted
- keep the code readable and avoid adding abstractions that do not help

Review the solution as if you were doing a senior engineer self-review before submission. Check these areas:
- correctness of calculations
- naming and readability
- dead code or unnecessary complexity
- error handling
- memory behavior for large files
- determinism of output ordering and formatting
Apply the improvements directly rather than only describing them.

Add validation and error handling for these cases:
- missing input file
- unreadable input file
- malformed header
- malformed row
- blank required fields
I want deterministic error messages that include line numbers where possible. Keep failure handling straightforward for a CLI tool.

Add tests that verify:
- campaign aggregation is correct
- top 10 CTR output is correct
- top 10 CPA output is correct
- CPA is blank when conversions are zero
- malformed rows fail with a clear error
- missing input file fails cleanly
Keep tests focused and easy to read.

Create a compact sample CSV input for manual verification and provide the exact expected outputs for all generated CSV files. The sample should be small but intentionally chosen to validate:
- repeated campaign IDs
- aggregation across multiple rows
- a zero-conversion case
- CTR ranking
- CPA ranking

I now want timing instrumentation for performance tracing. Add logging for:
- processing start
- CSV read + aggregate duration
- output write duration
- total processing duration
Use SLF4J. Keep the logs useful for measuring real runs on large files.

Switch the logging implementation to Lombok `@Slf4j`. Add only the configuration needed to support it in Maven and keep the rest of the code unchanged unless a change is necessary.

Use the Maven wrapper to verify the project with `clean package`. Do not guess if something fails. Reproduce the exact failure, identify the cause, and fix the real issue only. If the issue is caused by platform-specific line endings, make the generated output deterministic across operating systems.

Update `README.md` for submission. Write in concise technical English and include:
- setup instructions
- build command
- run command
- output files produced
- libraries used
- measured processing time for the 1GB file
Prefer exact commands and measurable statements over generic description.

Use this measured timing in the README:
- reading and aggregating CSV: `17006 ms`
- writing output files: `13 ms`
- total processing time: `17024 ms`

Review the final repository from the perspective of a hiring team evaluating a take-home assignment. Tell me whether the solution now demonstrates:
- correct implementation
- clear reasoning
- pragmatic design
- awareness of performance constraints
- ability to use AI effectively as a coding assistant
If there is any weak spot left, identify it precisely and suggest the smallest meaningful improvement.
