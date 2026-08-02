# ============================================================
# Import required libraries
# ============================================================

# Used to generate reproducible random numbers.
import random

# NumPy is used for mathematical operations and handling arrays.
import numpy as np

# Pandas is used to read and manipulate CSV datasets.
import pandas as pd

# TensorFlow is Google's Machine Learning framework.
# It is used to build, train and export the neural network.
import tensorflow as tf

# Used to split the dataset into training and testing datasets.
from sklearn.model_selection import train_test_split

# Used to evaluate the performance of the trained model.
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
)

# ============================================================
# Reproducibility
# ============================================================

# Fixed random seed.
# Every time this script runs, the model will generate
# almost identical results.
SEED = 42

# Set Python random generator.
random.seed(SEED)

# Set NumPy random generator.
np.random.seed(SEED)

# Set TensorFlow random generator.
tf.random.set_seed(SEED)

# ============================================================
# Security Constants
# (Must match Android SecurityConstants.kt)
# ============================================================

# These values are maximum allowed values used for normalization.
# The Android application uses exactly the same values,
# ensuring that training and prediction use identical scaling.

MAX_PERMISSION_COUNT = 50.0
MAX_DANGEROUS_PERMISSION_COUNT = 10.0

MAX_EXPORTED_ACTIVITY_COUNT = 10.0
MAX_EXPORTED_SERVICE_COUNT = 10.0
MAX_EXPORTED_RECEIVER_COUNT = 10.0
MAX_EXPORTED_PROVIDER_COUNT = 10.0

MAX_ANDROID_SDK = 50.0

# ============================================================
# Load Dataset
# ============================================================

# Read dataset.csv into memory as a Pandas DataFrame.
# Each row represents one Android application.
data = pd.read_csv("dataset.csv")

# Print number of rows and columns.
print("Dataset Shape :", data.shape)

# Print first five rows to verify data.
print(data.head())

# ============================================================
# Feature-wise Normalization
# ============================================================

# Neural networks perform better when features have similar scales.
# Therefore each numerical feature is converted into the range 0–1.

# ------------------------------------------------------------
# Permission Count
# ------------------------------------------------------------

# clip() limits values above MAX_PERMISSION_COUNT.
# Example:
# 60 permissions becomes 50.
#
# Division converts value into 0-1.
# Example:
# 25 / 50 = 0.50

data["perm_count"] = (
    data["perm_count"]
    .clip(upper=MAX_PERMISSION_COUNT)
    / MAX_PERMISSION_COUNT
)

# Dangerous permission count normalization.

data["danger_perm"] = (
    data["danger_perm"]
    .clip(upper=MAX_DANGEROUS_PERMISSION_COUNT)
    / MAX_DANGEROUS_PERMISSION_COUNT
)

# Exported Activity count normalization.

data["exp_act"] = (
    data["exp_act"]
    .clip(upper=MAX_EXPORTED_ACTIVITY_COUNT)
    / MAX_EXPORTED_ACTIVITY_COUNT
)

# Exported Service count normalization.

data["exp_serv"] = (
    data["exp_serv"]
    .clip(upper=MAX_EXPORTED_SERVICE_COUNT)
    / MAX_EXPORTED_SERVICE_COUNT
)

# Exported Broadcast Receiver normalization.

data["exp_recv"] = (
    data["exp_recv"]
    .clip(upper=MAX_EXPORTED_RECEIVER_COUNT)
    / MAX_EXPORTED_RECEIVER_COUNT
)

# Exported Content Provider normalization.

data["exp_prov"] = (
    data["exp_prov"]
    .clip(upper=MAX_EXPORTED_PROVIDER_COUNT)
    / MAX_EXPORTED_PROVIDER_COUNT
)

# Target Android SDK normalization.
# Example:
# SDK 35 becomes 35/50 = 0.70

data["target_sdk"] = (
    data["target_sdk"]
    / MAX_ANDROID_SDK
)

# Minimum SDK normalization.

data["min_sdk"] = (
    data["min_sdk"]
    / MAX_ANDROID_SDK
)

# Binary values (0 or 1) already fall between 0 and 1.
# Therefore no normalization is required.

# ============================================================
# Select Features
# ============================================================

# List of input features that the neural network will use.

FEATURES = [

    # Number of requested permissions
    "perm_count",

    # Number of dangerous permissions
    "danger_perm",

    # Exported Activities
    "exp_act",

    # Exported Services
    "exp_serv",

    # Exported Broadcast Receivers
    "exp_recv",

    # Exported Content Providers
    "exp_prov",

    # Application is debuggable
    "debuggable",

    # Backup is enabled
    "backup",

    # System application
    "system",

    # Cleartext HTTP traffic allowed
    "cleartext",

    # Target Android SDK
    "target_sdk",

    # Minimum Android SDK
    "min_sdk",

    # Sensitive permission categories
    "sms",
    "location",
    "contacts",
    "mic",
    "camera"
]

# Extract selected columns as input features (X).
# float32 is TensorFlow's preferred data type.

X = data[FEATURES].astype("float32").values

# Extract labels (0 = Safe, 1 = Risky).

y = data["label"].astype("float32").values

print(f"\nUsing {len(FEATURES)} features:")
print(FEATURES)

# ============================================================
# Train / Test Split
# ============================================================

# Divide dataset into two groups.
#
# Training set:
# Used for learning.
#
# Test set:
# Used only for final evaluation.

X_train, X_test, y_train, y_test = train_test_split(

    X,

    y,

    # 20% reserved for testing.
    test_size=0.20,

    # Makes split reproducible.
    random_state=SEED,

    # Preserves class balance.
    stratify=y,
)

# ============================================================
# Build Neural Network
# ============================================================

# Sequential means layers are connected one after another.

model = tf.keras.Sequential([

    # Input layer.
    # Number of neurons equals number of input features.

    tf.keras.layers.Input(shape=(len(FEATURES),)),

    # Hidden Layer 1
    # 32 neurons
    # ReLU introduces non-linearity.

    tf.keras.layers.Dense(
        32,
        activation="relu"
    ),

    # Hidden Layer 2
    # Learns higher-level feature combinations.

    tf.keras.layers.Dense(
        16,
        activation="relu"
    ),

    # Output Layer
    # One neuron
    # Sigmoid outputs probability between 0 and 1.

    tf.keras.layers.Dense(
        1,
        activation="sigmoid"
    )
])

# ============================================================
# Configure Training
# ============================================================

model.compile(

    # Adam automatically adjusts learning rate.
    optimizer="adam",

    # Binary Crossentropy is used for binary classification.
    loss="binary_crossentropy",

    # Measure prediction accuracy.
    metrics=[
        "accuracy"
    ]
)

# Print network architecture.

model.summary()

# ============================================================
# Callbacks
# ============================================================

# Callbacks automatically perform useful actions during training.

callbacks = [

    # Stops training if validation loss
    # stops improving for 5 epochs.

    tf.keras.callbacks.EarlyStopping(

        monitor="val_loss",

        patience=5,

        restore_best_weights=True

    ),

    # Save only the best performing model.

    tf.keras.callbacks.ModelCheckpoint(

        filepath="best_model.keras",

        monitor="val_accuracy",

        save_best_only=True
    )
]

# ============================================================
# Train Neural Network
# ============================================================

history = model.fit(

    # Training input
    X_train,

    # Training labels
    y_train,

    # Reserve 10% of training data
    # for validation.

    validation_split=0.10,

    # Maximum number of learning cycles.
    epochs=30,

    # Number of samples processed together.
    batch_size=8,

    # Enable callbacks.
    callbacks=callbacks,

    # Show training progress.
    verbose=1
)

# ============================================================
# Evaluate Model
# ============================================================

# Test the trained model using unseen data.

loss, accuracy = model.evaluate(

    X_test,

    y_test,

    verbose=0
)

print("\n==============================")
print("Evaluation")
print("==============================")

print(f"Loss      : {loss:.4f}")
print(f"Accuracy  : {accuracy:.4f}")

# ============================================================
# Make Predictions
# ============================================================

# Predict probability for each test sample.

probabilities = model.predict(X_test)

# Convert probability into class labels.
#
# >=0.5 becomes Risky (1)
# <0.5 becomes Safe (0)

predictions = (
    probabilities >= 0.5
).astype(int)

# ============================================================
# Performance Metrics
# ============================================================

print("\nClassification Report")

# Shows:
# Precision
# Recall
# F1-score
# Support

print(

    classification_report(

        y_test,

        predictions,

        digits=4
    )
)

print("\nConfusion Matrix")

# Shows:
# True Positive
# True Negative
# False Positive
# False Negative

print(

    confusion_matrix(

        y_test,

        predictions
    )
)

# ============================================================
# Export SavedModel
# ============================================================

# Save the complete TensorFlow model.

model.export("risk_model")

# ============================================================
# Convert to TensorFlow Lite
# ============================================================

# Create TensorFlow Lite converter.

converter = tf.lite.TFLiteConverter.from_saved_model(
    "risk_model"
)

# Convert TensorFlow model into lightweight TFLite format.

tflite_model = converter.convert()

# Save TFLite model to disk.

with open(
        "risk_model.tflite",
        "wb"
) as f:
    f.write(tflite_model)

print("\nTensorFlow Lite model exported successfully.")