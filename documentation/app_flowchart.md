flowchart TD
  LA[Launch App]
  LA --> AU{User Authenticated}
  AU -->|Yes| HD[Home Dashboard]
  AU -->|No| LS[Login Screen]
  LS -->|Login| AUth[Authenticate User]
  LS -->|Register| RG[Registration Screen]
  RG --> CA[Create Account]
  CA --> AUth
  AUth -->|Success| HD
  AUth -->|Fail| LS
  HD --> PR[View Profile]
  HD --> TL[Manage To-Do List]
  HD --> WV[View Weather]
  HD --> LO[Logout]
  PR --> EP[Edit Profile]
  EP --> PR
  PR --> HD
  TL --> TDLS[To-Do List Screen]
  TDLS --> CT[Create Task]
  TDLS --> ET[Edit Task]
  TDLS --> DT[Delete Task]
  CT --> TDLS
  ET --> TDLS
  DT --> TDLS
  WV --> FW[Fetch Weather Data]
  FW --> DW[Display Weather]
  FW --> EW[Show Error]
  DW --> HD
  EW --> HD
  LO --> LS