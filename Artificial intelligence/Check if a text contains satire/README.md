# Satire Detection System

<div align="center">

![Python](https://img.shields.io/badge/Python-3.x-blue?style=for-the-badge&logo=python)
![scikit-learn](https://img.shields.io/badge/scikit--learn-ML-orange?style=for-the-badge&logo=scikit-learn)
![Pandas](https://img.shields.io/badge/Pandas-Data-green?style=for-the-badge&logo=pandas)
![NumPy](https://img.shields.io/badge/NumPy-Math-blue?style=for-the-badge&logo=numpy)

**A machine learning classifier that detects satire in news articles using NLP and SVM. Because sometimes you need a computer to tell you what's a joke.**

[What It Does](#what-it-does) • [Tech Stack](#tech-stack) • [How It Works](#how-it-works) • [Performance](#performance)

</div>

---

## What It Does

This is a text classification system that analyzes news articles and determines whether they're satirical or legitimate. It uses Natural Language Processing (NLP) combined with a Linear Support Vector Machine to make predictions based on article titles and content.

**Key Features:**
- Binary classification (satire vs. non-satire)
- TF-IDF vectorization for text representation
- Pipeline-based architecture for clean data flow
- Handles missing values gracefully
- 80/20 train-test split for validation
- N-gram analysis (unigrams + bigrams)

**Use cases:**
- Content moderation systems
- News aggregator filtering
- Media literacy tools
- Academic research on satire detection

The model processes raw text through a pipeline: CountVectorizer → TF-IDF Transformer → Linear SVC, making predictions in milliseconds.

---

## Tech Stack

**Language:** Python 3.x  
**ML Framework:** scikit-learn  
**Data Processing:** pandas, NumPy  
**NLP Components:** CountVectorizer, TfidfTransformer  
**Classifier:** LinearSVC (Support Vector Machine)

### Architecture

Clean pipeline architecture with three stages:

```
Raw Text (title + content)
      ↓
CountVectorizer (n-gram extraction, 1-2 words)
      ↓
TfidfTransformer (term frequency weighting)
      ↓
Linear SVC (C=100.0, regularization)
      ↓
Prediction (0 = genuine, 1 = satire)
```

**How It Works:**
1. **Data Preprocessing:** Loads CSV data, fills missing values (empty strings for text, 0 for labels)
2. **Feature Extraction:** Converts text to frequency matrix using CountVectorizer with bigram support
3. **TF-IDF Transformation:** Weights terms by importance (term frequency-inverse document frequency)
4. **Training:** Fits LinearSVC with C=100.0 (strong regularization for generalization)
5. **Prediction:** Classifies new articles through the trained pipeline

**Key Parameters:**
- `ngram_range=(1, 2)` - Captures single words and two-word phrases
- `max_df=0.8` - Ignores terms appearing in >80% of documents (too common)
- `min_df=20` - Ignores rare terms appearing in <20 documents
- `C=100.0` - Regularization strength (inverse proportion)

---

## Project Structure

```
Check if a text contains satire/
├── Solve.ipynb           # Main notebook with full implementation
├── README.md             # This file
├── train.csv             # Training dataset (not included - too large)
└── test.csv              # Test dataset
```

---

## Getting Started

**Prerequisites:**
Python 3.x with pip

**Install dependencies:**
```bash
pip install numpy pandas scikit-learn
```

**Run the notebook:**
```bash
jupyter notebook Solve.ipynb
```

**Or run as a script:**
```python
import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.svm import LinearSVC

# Load training data
trainCsv = pd.read_csv("train.csv", index_col="id")
trainCsv['title'] = trainCsv['title'].fillna("")
trainCsv['content'] = trainCsv['content'].fillna("")
trainCsv['class'] = trainCsv['class'].fillna(0)

# Split data
textTrain, textTest, yTrain, yTest = train_test_split(
    trainCsv["title"] + trainCsv["content"], 
    trainCsv["class"], 
    test_size=0.2, 
    random_state=42
)

# Build pipeline
trainModel = Pipeline([
    ('vect', CountVectorizer(ngram_range=(1, 2), max_df=0.8, min_df=20)),
    ('tfidf', TfidfTransformer()),
    ('svMachine', LinearSVC(C=100.0))
])

# Train
trainModel.fit(textTrain, yTrain)

# Evaluate
print(f"Accuracy: {trainModel.score(textTest, yTest):.2%}")
```

---

## Performance

**Complexity Analysis:**

- **CountVectorizer:** $O(N \times V)$ where $N$ = number of documents, $V$ = vocabulary size
- **TfidfTransformer:** $O(N \times V)$ for matrix transformation
- **Linear SVC Training:** $O(N^2)$ to $O(N^3)$ depending on convergence
- **Prediction:** $O(V)$ per document (very fast)

**Space Complexity:**
- Feature matrix: $O(N \times V)$ (sparse representation)
- SVM model: $O(S \times V)$ where $S$ = number of support vectors

**Overall:** Training is $O(N^2)$ to $O(N^3)$, but prediction is near-instantaneous. The pipeline is optimized for production use with sparse matrices.

---

## Dataset

The project uses a CSV dataset with three columns:
- `id` - Unique article identifier
- `title` - Article headline
- `content` - Full article text
- `class` - Label (0 = genuine, 1 = satire)

**Note:** Training data is too large for GitHub. You'll need to provide your own labeled satire dataset.

---

## What's Next

Potential improvements:
- Deep learning approach (BERT, RoBERTa) for better accuracy
- MultiPython script version with CLI interface
- Web API with Flask/FastAPI
- Model persistence (pickle/joblib) for deployment
- Cross-validation for robust evaluation
- Feature importance analysis
- Support for multiple languages

---

## License

This code is proprietary and may not be copied, distributed, or modified without express written permission from the author.