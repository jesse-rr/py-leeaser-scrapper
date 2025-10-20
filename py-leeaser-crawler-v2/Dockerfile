# --- Base Image ---
FROM python:3.11-slim

# --- Environment ---
ENV DEBIAN_FRONTEND=noninteractive
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# --- Install system dependencies needed for Chrome ---
RUN apt-get update && apt-get install -y \
    wget \
    ca-certificates \
    fonts-liberation \
    libnss3 \
    libxss1 \
    libasound2 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libglib2.0-0 \
    libx11-6 \
    libxcb1 \
    libxdamage1 \
    libxrandr2 \
    libgbm1 \
    unzip \
    python3-setuptools \
    && rm -rf /var/lib/apt/lists/*

# --- Set work directory ---
WORKDIR /app

# --- Copy project files ---
COPY requirements.txt .
COPY . .

# --- Install Python dependencies ---
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

# --- Default command ---
CMD ["python", "main.py"]
