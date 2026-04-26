import os
import subprocess
from decimal import Decimal


def parse_output(line: str) -> dict[str, Decimal]:
    parts = line.strip().split()
    values = {}
    for part in parts:
        key, raw = part.split("=")
        values[key] = Decimal(raw)
    return values


def test_cli_pricing_flow() -> None:
    env = os.environ.copy()
    java_home = "/root/.local/share/mise/installs/java/21.0.2"
    env["JAVA_HOME"] = java_home
    env["PATH"] = f"{java_home}/bin:{env['PATH']}"

    result = subprocess.run(
        [
            "gradle",
            "-q",
            "run",
            "--args=VIP SAVE10 100x1 25x2",
        ],
        capture_output=True,
        text=True,
        check=True,
        env=env,
    )

    parsed = parse_output(result.stdout)
    assert parsed["subtotal"] == Decimal("150.00")
    assert parsed["discount"] == Decimal("22.50")
    assert parsed["tax"] == Decimal("10.20")
    assert parsed["final"] == Decimal("137.70")
